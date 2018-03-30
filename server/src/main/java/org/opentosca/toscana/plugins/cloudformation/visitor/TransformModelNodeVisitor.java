package org.opentosca.toscana.plugins.cloudformation.visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.model.artifact.Artifact;
import org.opentosca.toscana.model.capability.ComputeCapability;
import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.model.node.Apache;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.Database;
import org.opentosca.toscana.model.node.Dbms;
import org.opentosca.toscana.model.node.MysqlDatabase;
import org.opentosca.toscana.model.node.Nodejs;
import org.opentosca.toscana.model.node.WebApplication;
import org.opentosca.toscana.model.node.custom.JavaApplication;
import org.opentosca.toscana.model.visitor.NodeVisitor;
import org.opentosca.toscana.plugins.cloudformation.CloudFormationModule;
import org.opentosca.toscana.plugins.cloudformation.handler.OperationHandler;
import org.opentosca.toscana.plugins.cloudformation.mapper.CapabilityMapper;
import org.opentosca.toscana.plugins.cloudformation.mapper.JavaRuntimeMapper;
import org.opentosca.toscana.plugins.util.TransformationFailureException;

import com.amazonaws.SdkClientException;
import com.scaleset.cfbuilder.beanstalk.Application;
import com.scaleset.cfbuilder.beanstalk.ApplicationVersion;
import com.scaleset.cfbuilder.beanstalk.ConfigurationTemplate;
import com.scaleset.cfbuilder.beanstalk.Environment;
import com.scaleset.cfbuilder.beanstalk.OptionSetting;
import com.scaleset.cfbuilder.beanstalk.SourceBundle;
import com.scaleset.cfbuilder.ec2.Instance;
import com.scaleset.cfbuilder.ec2.SecurityGroup;
import com.scaleset.cfbuilder.ec2.metadata.CFNCommand;
import com.scaleset.cfbuilder.ec2.metadata.CFNInit;
import com.scaleset.cfbuilder.ec2.metadata.CFNPackage;
import com.scaleset.cfbuilder.rds.DBInstance;

import static org.opentosca.toscana.plugins.cloudformation.CloudFormationLifecycle.toAlphanumerical;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.CONFIG_CONFIGURE;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.CONFIG_CREATE;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.CONFIG_SETS;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.CONFIG_START;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.FILEPATH_NODEJS_CREATE;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.SECURITY_GROUP;
import static org.opentosca.toscana.plugins.cloudformation.handler.EnvironmentHandler.APACHE_ENV_IMPORT;
import static org.opentosca.toscana.plugins.cloudformation.handler.OperationHandler.APACHE_RESTART_COMMAND;

/**
 Transforms a models nodes.
 <br>
 Performs the transformation using the prepared {@link org.opentosca.toscana.model.EffectiveModel} and the already
 modified {@link CloudFormationModule}.
 */
public class TransformModelNodeVisitor extends CloudFormationVisitor implements NodeVisitor {
    private static final String IP_OPEN = "0.0.0.0/0";
    private static final String PROTOCOL_TCP = "tcp";
    private OperationHandler operationHandler;

    /**
     Creates a <tt>TransformModelNodeVisitor<tt> in order to build a template with the given
     <tt>CloudFormationModule<tt>.

     @param context   {@link TransformationContext} to extract the topology and a logger
     @param cfnModule {@link CloudFormationModule} to build the template model
     */
    public TransformModelNodeVisitor(TransformationContext context, CloudFormationModule cfnModule) {
        super(context, cfnModule);
        this.operationHandler = new OperationHandler(cfnModule, logger);
    }

    /**
     Transforms the {@link Compute} node into a EC2 Instance.

     @param node the {@link Compute} node to visit
     */
    @Override
    public void visit(Compute node) {
        try {
            if (cfnModule.checkComputeToEc2(node)) {
                logger.debug("Compute '{}' will be transformed to EC2", node.getEntityName());
                String nodeName = toAlphanumerical(node.getEntityName());
                //default security group the EC2 Instance
                SecurityGroup webServerSecurityGroup = cfnModule.resource(SecurityGroup.class,
                    nodeName + SECURITY_GROUP)
                    .groupDescription("Enables ports for " + nodeName + ".");
                //open endpoint port
                node.getEndpoint().getPort().ifPresent(
                    port -> webServerSecurityGroup
                        .ingress(ingress -> ingress.cidrIp(IP_OPEN), PROTOCOL_TCP, port.port)
                );
                // check what image id should be taken
                CapabilityMapper capabilityMapper = createCapabilityMapper();
                OsCapability computeOs = node.getOs();
                String imageId = capabilityMapper.mapOsCapabilityToImageId(computeOs);
                ComputeCapability computeCompute = node.getHost();
                String instanceType = capabilityMapper.mapComputeCapabilityToInstanceType(computeCompute,
                    CapabilityMapper.EC2_DISTINCTION);
                //create CFN init and store it
                CFNInit init = new CFNInit(CONFIG_SETS);
                cfnModule.putCFNInit(nodeName, init);
                cfnModule.resource(Instance.class, nodeName)
                    .securityGroupIds(webServerSecurityGroup)
                    .imageId(imageId)
                    .instanceType(instanceType);
                capabilityMapper.mapDiskSize(computeCompute, cfnModule, nodeName);
                // Add Reference to keyName if KeyPair needed and open Port 22 (Allows SSH access)
                if (cfnModule.hasKeyPair()) {
                    Instance instance = (Instance) cfnModule.getResource(nodeName);
                    instance.keyName(cfnModule.getKeyNameVar());
                    webServerSecurityGroup
                        .ingress(ingress -> ingress.cidrIp(IP_OPEN), PROTOCOL_TCP, 22);
                }
            } else {
                logger.debug("Compute '{}' will not be transformed to EC2", node.getEntityName());
            }
        } catch (SdkClientException se) {
            logger.error("SDKClient failed, no valid credentials or no internet connection");
            throw new TransformationFailureException("Failed", se);
        } catch (Exception e) {
            logger.error("Error while creating EC2Instance resource.");
            throw new TransformationFailureException("Failed at Compute node " + node.getEntityName(), e);
        }
    }

    /**
     Transforms the {@link Database} node by moving the lifecycle operations to its host.
     <br>
     Also opens the specified database port on its host to everyone who has a connection to this node.

     @param node the {@link Database} node to visit
     */
    @Override
    public void visit(Database node) {
        try {
            Compute computeHost = getCompute(node);
            String computeHostName = toAlphanumerical(computeHost.getEntityName());
            operationHandler.handleGenericHostedNode(node, computeHost);

            //Open Database port
            String SecurityGroupName = computeHostName + SECURITY_GROUP;
            SecurityGroup securityGroup = (SecurityGroup) cfnModule.getResource(SecurityGroupName);
            if (node.getPort().isPresent()) {
                Integer databasePort = node.getPort().orElseThrow(() -> new IllegalArgumentException("Database " +
                    "port not set"));
                Set<Compute> hostsOfConnectedTo = getHostsOfConnectedTo(node);
                for (Compute hostOfConnectedTo : hostsOfConnectedTo) {
                    securityGroup.ingress(ingress -> ingress.sourceSecurityGroupName(
                        cfnModule.ref(toAlphanumerical(hostOfConnectedTo.getEntityName()) + SECURITY_GROUP)),
                        PROTOCOL_TCP,
                        databasePort);
                }
            }
        } catch (Exception e) {
            logger.error("Error while creating Database resource.");
            throw new TransformationFailureException("Failed at Database node " + node.getEntityName(), e);
        }
    }

    /**
     Transforms the {@link MysqlDatabase} node into a AWS RDS instance.
     <br>
     Also handles its sql artifact.

     @param node the {@link MysqlDatabase} node to visit
     */
    @Override
    public void visit(MysqlDatabase node) {
        try {
            String nodeName = toAlphanumerical(node.getEntityName());
            //get the compute where the dbms this node is hosted on, is hosted on
            Compute compute = getCompute(node);
            String serverName = toAlphanumerical(compute.getEntityName());
            String dbName = node.getDatabaseName();
            String masterUser = node.getUser().orElseThrow(() -> new IllegalArgumentException("Database user not set"));
            String masterPassword = node.getPassword().orElseThrow(() -> new IllegalArgumentException("Database " +
                "password not set"));
            Integer port = node.getPort().orElseThrow(() -> new IllegalArgumentException("Database port not set"));
            //check what values should be taken
            ComputeCapability hostedOnComputeCapability = compute.getHost();
            CapabilityMapper capabilityMapper = createCapabilityMapper();
            String dBInstanceClass = capabilityMapper.mapComputeCapabilityToInstanceType(hostedOnComputeCapability,
                CapabilityMapper.RDS_DISTINCTION);
            Integer allocatedStorage = capabilityMapper.mapComputeCapabilityToRDSAllocatedStorage
                (hostedOnComputeCapability);
            String storageType = "gp2"; //SSD

            String securityGroupName = nodeName + SECURITY_GROUP;
            SecurityGroup securityGroup = cfnModule.resource(SecurityGroup.class, securityGroupName)
                .groupDescription("Open database " + dbName + " for access to group " + serverName + SECURITY_GROUP);
            Set<Compute> hostsOfConnectedTo = getHostsOfConnectedTo(node);
            for (Compute hostOfConnectedTo : hostsOfConnectedTo) {
                logger.debug("Open connection to " + toAlphanumerical(hostOfConnectedTo.getEntityName()) + SECURITY_GROUP);
                securityGroup.ingress(ingress -> ingress.sourceSecurityGroupName(
                    cfnModule.ref(toAlphanumerical(hostOfConnectedTo.getEntityName()) + SECURITY_GROUP)),
                    PROTOCOL_TCP,
                    port);
            }
            cfnModule.resource(DBInstance.class, nodeName)
                .engine("MySQL")
                .dBName(dbName)
                .masterUsername(masterUser)
                .masterUserPassword(masterPassword)
                .dBInstanceClass(dBInstanceClass)
                .allocatedStorage(allocatedStorage)
                .storageType(storageType)
                .vPCSecurityGroups(cfnModule.fnGetAtt(securityGroupName, "GroupId"));
            //handle sql artifact
            for (Artifact artifact : node.getArtifacts()) {
                String relPath = artifact.getFilePath();
                if (relPath.endsWith(".sql")) {
                    String sql = cfnModule.getFileAccess().read(artifact.getFilePath());
                    String computeName = createSqlEc2(node, sql);
                    securityGroup.ingress(ingress -> ingress.sourceSecurityGroupName(
                        cfnModule.ref(toAlphanumerical(computeName) + SECURITY_GROUP)),
                        PROTOCOL_TCP,
                        port);
                }
            }
        } catch (Exception e) {
            logger.error("Error while creating MysqlDatabase resource.");
            throw new TransformationFailureException("Failed at MysqlDatabase node " + node.getEntityName(), e);
        }
    }

    /**
     Transforms the {@link Database} node by moving the lifecycle operations to its host.
     <br>
     Also opens the specified dbms port on its host to everyone.

     @param node the {@link Dbms} node to visit
     */
    @Override
    public void visit(Dbms node) {
        try {
            //get the compute where the dbms this node is hosted on, is hosted on
            Compute computeHost = getCompute(node);
            String computeHostName = toAlphanumerical(computeHost.getEntityName());
            operationHandler.handleGenericHostedNode(node, computeHost);

            //Open Dbms port
            String SecurityGroupName = computeHostName + SECURITY_GROUP;
            SecurityGroup securityGroup = (SecurityGroup) cfnModule.getResource(SecurityGroupName);
            if (node.getPort().isPresent()) {
                Integer dbmsPort = node.getPort().orElseThrow(() -> new IllegalArgumentException("Database " +
                    "port not set"));
                securityGroup.ingress(ingress -> ingress.cidrIp(IP_OPEN), PROTOCOL_TCP, dbmsPort);
            }
        } catch (Exception e) {
            logger.error("Error while creating Dbms resource.");
            throw new TransformationFailureException("Failed at Dbms node " + node.getEntityName(), e);
        }
    }

    /**
     Transforms the {@link Apache} node by moving the configure and start lifecycle operations to its host.
     <br>
     Instead of the create lifecycle it installs apache2 on the host. Also the command to put the environment variables
     from the system to the apache is added to the host.

     @param node the {@link Apache} node to visit
     */
    @Override
    public void visit(Apache node) {
        try {
            Compute compute = getCompute(node);
            String computeName = toAlphanumerical(compute.getEntityName());
            //instead of lifecycle create we add the package apache2 to the configset
            cfnModule.getCFNInit(computeName)
                .getOrAddConfig(CONFIG_SETS, CONFIG_CREATE)
                .putPackage(
                    //TODO apt only if linux
                    new CFNPackage("apt")
                        .addPackage("apache2"));

            //handle configure
            operationHandler.handleConfigure(node, computeName);
            //handle start
            operationHandler.handleStart(node, computeName);
            //Source environment variables in /etc/apache/envvars from /etc/environment and restart apache2 directly 
            // afterwards
            cfnModule.getCFNInit(computeName)
                .getOrAddConfig(CONFIG_SETS, CONFIG_CONFIGURE)
                .putCommand(new CFNCommand("Add Apache environment variables", APACHE_ENV_IMPORT));
            //we add restart apache2 command to the configscript if start or configure existed
            if (node.getStandardLifecycle().getConfigure().isPresent() || node.getStandardLifecycle().getStart()
                .isPresent()) {
                cfnModule.getCFNInit(computeName)
                    .getOrAddConfig(CONFIG_SETS, CONFIG_START)
                    .putCommand(new CFNCommand("restart apache2", APACHE_RESTART_COMMAND));
            }
        } catch (Exception e) {
            logger.error("Error while creating Apache");
            throw new TransformationFailureException("Failed at Apache node " + node.getEntityName(), e);
        }
    }

    /**
     Transforms the {@link WebApplication} node by moving the lifecycle operations to its host.
     <br>
     Also opens the host to the application endpoint port.

     @param node the {@link WebApplication} node to visit
     */
    @Override
    public void visit(WebApplication node) {
        try {
            //get the compute where the apache this node is hosted on, is hosted on
            Compute compute = getCompute(node);
            String computeName = toAlphanumerical(compute.getEntityName());
            node.getAppEndpoint().getPort().ifPresent(port -> {
                SecurityGroup computeSecurityGroup = (SecurityGroup) cfnModule.getResource(computeName + SECURITY_GROUP);
                computeSecurityGroup.ingress(ingress -> ingress.cidrIp(IP_OPEN), PROTOCOL_TCP, port.port);
            });
            //handle create
            operationHandler.handleCreate(node, computeName);
            //handle configure
            operationHandler.handleConfigure(node, computeName);
            //handle start
            operationHandler.handleStart(node, computeName);
        } catch (Exception e) {
            logger.error("Error while creating WebApplication");
            throw new TransformationFailureException("Failed at WebApplication node " + node.getEntityName(), e);
        }
    }

    /**
     Transforms the {@link Nodejs} node by moving the configure and start lifecycle operations to its host.
     <br>
     Instead of the create lifecycle it executes the nodejs create script. Also opens the ports on its host.

     @param node the {@link Nodejs} node to visit
     */
    @Override
    public void visit(Nodejs node) {
        try {
            Compute computeHost = getCompute(node);
            String computeHostName = toAlphanumerical(computeHost.getEntityName());

            //handle configure
            operationHandler.handleConfigure(node, computeHostName);
            //handle start
            operationHandler.handleStart(node, computeHostName);
            //add NodeJs create script
            operationHandler.addCreate(FILEPATH_NODEJS_CREATE, computeHostName);

            //Get ports
            List<Integer> portList = getPortsFromEnpointCapability(node);
            //Open ports
            String SecurityGroupName = computeHostName + SECURITY_GROUP;
            SecurityGroup securityGroup = (SecurityGroup) cfnModule.getResource(SecurityGroupName);
            securityGroup.ingress(ingress -> ingress.cidrIp(IP_OPEN), PROTOCOL_TCP, portList.toArray());
        } catch (Exception e) {
            logger.error("Error while creating Nodejs");
            throw new TransformationFailureException("Failed at Nodejs node " + node.getEntityName(), e);
        }
    }

    /**
     Transforms the {@link JavaApplication} node using AWS Beanstalk.
     <br>
     A Beanstalk Application, Configuration Template and Environment are created. A SecurityGroup is made and assigned
     to the OptionSettings as well as the environment variables that should be pushed. OptionSettings are settings that
     influence the Beanstalk Application.

     @param node the {@link JavaApplication} node to visit
     */
    @Override
    public void visit(JavaApplication node) {
        try {
            String nodeName = toAlphanumerical(node.getEntityName());
            Application beanstalkApplication = cfnModule.resource(Application.class, nodeName)
                .description("JavaApplication " + nodeName);
            SourceBundle sourceBundle = operationHandler.handleJarArtifact(node.getJar());
            ApplicationVersion beanstalkApplicationVersion = cfnModule.resource(ApplicationVersion.class, nodeName + "Version")
                .applicationName(beanstalkApplication)
                .description("JavaApplicationVersion " + nodeName)
                .sourceBundle(sourceBundle);
            JavaRuntimeMapper javaRuntimeMapper = new JavaRuntimeMapper(logger);
            String stackConfig = javaRuntimeMapper.mapRuntimeToStackConfig(getJavaRuntime(node));
            List<OptionSetting> optionSettings = new ArrayList<>();
            ConfigurationTemplate beanstalkConfigurationTemplate = cfnModule.resource(ConfigurationTemplate.class, nodeName + "ConfigurationTemplate")
                .applicationName(beanstalkApplication)
                .description("JavaApplicationConfigurationTemplate " + nodeName)
                .solutionStackName(stackConfig);
            //add securitygroup
            String hostComputeName = toAlphanumerical(getCompute(node).getEntityName());
            SecurityGroup beanstalkSecurityGroup = cfnModule.resource(SecurityGroup.class,
                hostComputeName + SECURITY_GROUP)
                .groupDescription("SecurityGroup for Beanstalk application " + nodeName + ".");
            //get and open ports
            List<Integer> portList = getPortsFromEnpointCapability(node);
            beanstalkSecurityGroup.ingress(ingress -> ingress.cidrIp(IP_OPEN), PROTOCOL_TCP, portList.toArray());
            //set securitygroup for beanstalk application
            optionSettings.add(new OptionSetting("aws:autoscaling:launchconfiguration", "SecurityGroups").setValue
                (cfnModule.ref(hostComputeName + SECURITY_GROUP)));
            //get environment variables as option settings
            optionSettings.addAll(operationHandler.handleStartJava(node));
            //add all option settings to beanstalk configuration
            if (!optionSettings.isEmpty()) {
                beanstalkConfigurationTemplate.optionSettings(optionSettings.toArray(new OptionSetting[0]));
            }
            cfnModule.resource(Environment.class, nodeName + "Environment")
                .applicationName(beanstalkApplication)
                .description("JavaApplicationEnvironment")
                .templateName(beanstalkConfigurationTemplate)
                .versionLabel(beanstalkApplicationVersion);
        } catch (Exception e) {
            logger.error("Error while creating JavaApplication");
            throw new TransformationFailureException("Failed at JavaApplication node " + node.getEntityName(), e);
        }
    }
    
    /**
     Creates a new {@link CapabilityMapper} with the {@link CloudFormationModule}s region and credentials.

     @return the created {@link CloudFormationModule}
     */
    public CapabilityMapper createCapabilityMapper() {
        return new CapabilityMapper(cfnModule.getAWSRegion(), cfnModule.getAwsCredentials(), logger);
    }
}
