package org.opentosca.toscana.plugins.cloudformation.visitor;

import java.io.File;
import java.io.IOException;

import org.opentosca.toscana.model.capability.ComputeCapability;
import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.model.node.Apache;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.Dbms;
import org.opentosca.toscana.model.node.MysqlDatabase;
import org.opentosca.toscana.model.node.MysqlDbms;
import org.opentosca.toscana.model.node.WebApplication;
import org.opentosca.toscana.model.node.WebServer;
import org.opentosca.toscana.model.operation.Operation;
import org.opentosca.toscana.model.operation.OperationVariable;
import org.opentosca.toscana.model.requirement.Requirement;
import org.opentosca.toscana.model.visitor.StrictNodeVisitor;
import org.opentosca.toscana.plugins.cloudformation.CloudFormationModule;
import org.opentosca.toscana.plugins.cloudformation.mapper.CapabilityMapper;
import org.opentosca.toscana.plugins.util.TransformationFailureException;

import com.amazonaws.SdkClientException;
import com.scaleset.cfbuilder.ec2.Instance;
import com.scaleset.cfbuilder.ec2.SecurityGroup;
import com.scaleset.cfbuilder.ec2.metadata.CFNCommand;
import com.scaleset.cfbuilder.ec2.metadata.CFNFile;
import com.scaleset.cfbuilder.ec2.metadata.CFNInit;
import com.scaleset.cfbuilder.ec2.metadata.CFNPackage;
import com.scaleset.cfbuilder.rds.DBInstance;
import org.slf4j.Logger;

import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.CONFIG_CONFIGURE;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.CONFIG_CREATE;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.CONFIG_SETS;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.CONFIG_START;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.SECURITY_GROUP;

/**
 Class for building a CloudFormation template from an effective model instance via the visitor pattern. Currently only
 supports LAMP-stacks built with Compute, WebApplication, Apache, MySQL, MySQL nodes.
 */
public class CloudFormationNodeVisitor implements StrictNodeVisitor {

    private final Logger logger;
    private CloudFormationModule cfnModule;

    /**
     Creates a <tt>CloudFormationNodeVisitor<tt> in order to build a template with the given
     <tt>CloudFormationModule<tt>.

     @param logger    Logger for logging visitor behaviour
     @param cfnModule Module to build the template model
     */
    public CloudFormationNodeVisitor(Logger logger, CloudFormationModule cfnModule) {
        this.logger = logger;
        this.cfnModule = cfnModule;
    }

    @Override
    public void visit(Compute node) {
        try {
            logger.info("Visit Compute node {}.", node.getEntityName());
            String nodeName = toAlphanumerical(node.getEntityName());
            //default security group the EC2 Instance opens for port 80 and 22 to the whole internet
            Object cidrIp = "0.0.0.0/0";
            SecurityGroup webServerSecurityGroup = cfnModule.resource(SecurityGroup.class,
                nodeName + SECURITY_GROUP)
                .groupDescription("Enable ports 80 and 22")
                .ingress(ingress -> ingress.cidrIp(cidrIp), "tcp", 80, 22);

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
            //takes default 8GB storage but volume
            cfnModule.resource(Instance.class, nodeName)
                .keyName(cfnModule.getKeyNameVar())
                .securityGroupIds(webServerSecurityGroup)
                .imageId(imageId)
                .instanceType(instanceType);
        } catch (SdkClientException se) {
            logger.error("SDKClient failed, no valid credentials or no internet connection");
            throw new TransformationFailureException("Failed", se);
        } catch (Exception e) {
            logger.error("Error while creating EC2Instance resource.");
            throw new TransformationFailureException("Failed at Compute node", e);
        }
    }

    @Override
    public void visit(MysqlDatabase node) {
        try {
            logger.info("Visit MysqlDatabase node {}.", node.getEntityName());
            String nodeName = toAlphanumerical(node.getEntityName());

            //get the name of the server where the dbms this node is hosted on, is hosted on
            String serverName;
            ComputeCapability hostedOnComputeCapability;
            if (exactlyOneFulfiller(node.getHost())) {
                Dbms dbms = node.getHost().getFulfillers().iterator().next();
                if (exactlyOneFulfiller(dbms.getHost())) {
                    Compute compute = dbms.getHost().getFulfillers().iterator().next();
                    serverName = toAlphanumerical(compute.getEntityName());
                    hostedOnComputeCapability = compute.getHost();
                } else {
                    throw new IllegalStateException("Got " + dbms.getHost().getFulfillers().size() + " instead of one" +
                        " fulfiller");
                }
            } else {
                throw new IllegalStateException("Got " + node.getHost().getFulfillers().size() + " instead of one " +
                    "fulfiller");
            }
            String dbName = node.getDatabaseName();
            String masterUser = node.getUser().orElseThrow(() -> new IllegalArgumentException("Database user not set"));
            String masterPassword = node.getPassword().orElseThrow(() -> new IllegalArgumentException("Database " +
                "password not set"));
            Integer port = node.getPort().orElse(3306);
            //check what values should be taken
            CapabilityMapper capabilityMapper = createCapabilityMapper();
            String dBInstanceClass = capabilityMapper.mapComputeCapabilityToInstanceType(hostedOnComputeCapability,
                CapabilityMapper.RDS_DISTINCTION);
            Integer allocatedStorage = capabilityMapper.mapComputeCapabilityToRDSAllocatedStorage
                (hostedOnComputeCapability);
            String storageType = "gp2"; //SSD

            String securityGroupName = nodeName + SECURITY_GROUP;
            cfnModule.resource(SecurityGroup.class, securityGroupName)
                .groupDescription("Open database " + dbName + " for access to group " + serverName + SECURITY_GROUP)
                .ingress(ingress -> ingress.sourceSecurityGroupName(cfnModule.ref(serverName + SECURITY_GROUP)),
                    "tcp", port);

            cfnModule.resource(DBInstance.class, nodeName)
                .engine("MySQL")
                .dBName(dbName)
                .masterUsername(masterUser)
                .masterUserPassword(masterPassword)
                .dBInstanceClass(dBInstanceClass)
                .allocatedStorage(allocatedStorage)
                .storageType(storageType)
                .vPCSecurityGroups(cfnModule.fnGetAtt(securityGroupName, "GroupId"));
        } catch (Exception e) {
            logger.error("Error while creating DBInstance resource.");
            throw new TransformationFailureException("Failed at MysqlDatabase node", e);
        }
    }

    @Override
    public void visit(MysqlDbms node) {
        logger.info("Visit MysqlDbms node {}.", node.getEntityName());
        // TODO handle sql artifact if present
    }

    @Override
    public void visit(Apache node) {
        try {
            logger.info("Visit Apache node " + node.getEntityName() + ".");
            String computeName;
            if (exactlyOneFulfiller(node.getHost())) {
                Compute compute = node.getHost().getFulfillers().iterator().next();
                computeName = toAlphanumerical(compute.getEntityName());
            } else {
                throw new IllegalStateException("Got " + node.getHost().getFulfillers().size() + " instead of one " +
                    "fulfiller");
            }

            //instead of lifecycle create we add the package apache2 to the configset
            cfnModule.getCFNInit(computeName)
                .getOrAddConfig(CONFIG_SETS, CONFIG_CREATE)
                .putPackage(
                    //TODO apt only if linux
                    new CFNPackage("apt")
                        .addPackage("apache2"));
            //handle configure
            if (node.getStandardLifecycle().getConfigure().isPresent()) {
                Operation configure = node.getStandardLifecycle().getConfigure().get();
                handleOperation(configure, computeName, CONFIG_CONFIGURE);
            }
            //handle start
            if (node.getStandardLifecycle().getStart().isPresent()) {
                Operation start = node.getStandardLifecycle().getStart().get();
                handleOperation(start, computeName, CONFIG_START);
            }
            //we add restart apache2 command to the configscript
            cfnModule.getCFNInit(computeName)
                .getOrAddConfig(CONFIG_SETS, CONFIG_START)
                .putCommand(new CFNCommand("restart apache2", "service apache2 restart"));
        } catch (Exception e) {
            logger.error("Error while creating Apache");
            throw new TransformationFailureException("Failed at Apache node", e);
        }
    }

    @Override
    public void visit(WebApplication node) {
        logger.info("Visit WebApplication node {}.", node.getEntityName());
        try {
            //get the name of the server where this node is hosted on
            String computeName;
            if (exactlyOneFulfiller(node.getHost())) {
                WebServer webServer = node.getHost().getFulfillers().iterator().next();
                if (exactlyOneFulfiller(webServer.getHost())) {
                    Compute compute = webServer.getHost().getFulfillers().iterator().next();
                    computeName = toAlphanumerical(compute.getEntityName());
                } else {
                    throw new IllegalStateException("Got " + webServer.getHost().getFulfillers().size() + " instead " +
                        "of one fulfiller");
                }
            } else {
                throw new IllegalStateException("Got " + node.getHost().getFulfillers().size() + " instead of one " +
                    "fulfiller");
            }
            //handle create
            if (node.getStandardLifecycle().getCreate().isPresent()) {
                Operation create = node.getStandardLifecycle().getCreate().get();
                handleOperation(create, computeName, CONFIG_CREATE);
            }
            //handle configure
            if (node.getStandardLifecycle().getConfigure().isPresent()) {
                Operation configure = node.getStandardLifecycle().getConfigure().get();
                handleOperation(configure, computeName, CONFIG_CONFIGURE);
            }
            //handle start
            if (node.getStandardLifecycle().getStart().isPresent()) {
                Operation start = node.getStandardLifecycle().getStart().get();
                handleOperation(start, computeName, CONFIG_START);
            }
        } catch (Exception e) {
            logger.error("Error while creating WebApplication");
            throw new TransformationFailureException("Failed at WebApplication node", e);
        }
    }

    private String toAlphanumerical(String inp) {
        return inp.replaceAll("[^A-Za-z0-9]", "");
    }

    private boolean exactlyOneFulfiller(Requirement requirement) {
        return (requirement.getFulfillers().size() == 1);
    }

    private void handleOperation(Operation operation, String serverName, String config) throws IOException {
        String cfnFilePath = "/home/ubuntu/"; // TODO Check what path is needed

        //Add dependencies
        for (String dependency : operation.getDependencies()) {
                String cfnFileMode = "000644"; //TODO Check what mode is needed (only read?)
                String cfnFileOwner = "root"; //TODO Check what Owner is needed
                String cfnFileGroup = "root"; //TODO Check what Group is needed

                // TODO: re-allow content-dumping instead of source
                CFNFile cfnFile = new CFNFile(cfnFilePath + dependency)
                    .setContent(cfnModule.getFileAccess().read(dependency))
                    .setMode(cfnFileMode)
                    .setOwner(cfnFileOwner)
                    .setGroup(cfnFileGroup);

                // Add file to install
                cfnModule.getCFNInit(serverName)
                    .getOrAddConfig(CONFIG_SETS, config)
                    .putFile(cfnFile);
        }

        //Add artifact
        if (operation.getArtifact().isPresent()) {
            String artifact = operation.getArtifact().get().getFilePath();

            String cfnFileMode = "000500"; //TODO Check what mode is needed (read? + execute?)
            String cfnFileOwner = "root"; //TODO Check what Owner is needed
            String cfnFileGroup = "root"; //TODO Check what Group is needed

                // TODO: re-allow content-dumping instead of source
                CFNFile cfnFile = new CFNFile(cfnFilePath + artifact)
                    .setContent(cfnModule.getFileAccess().read(artifact))
                    .setMode(cfnFileMode)
                    .setOwner(cfnFileOwner)
                    .setGroup(cfnFileGroup);

                CFNCommand cfnCommand = new CFNCommand(artifact,
                    cfnFilePath + artifact) //file is the full path, so need for "./"
                    .setCwd(cfnFilePath + new File(artifact).getParent());
                // add inputs to environment, but where to get other needed variables?
                for (OperationVariable input : operation.getInputs()) {
                    Object value = input.getValue().orElse(""); //TODO add default
                    if (("127.0.0.1".equals(value) || "localhost".equals(value)) && input.getKey().contains("host")) {
                        value = cfnModule.fnGetAtt("mydb", "Endpoint.Address"); //TODO how to handle this? with the 
                        // new model we should be able to get the reference
                    }
                    cfnCommand.addEnv(input.getKey(), value);
                }
                cfnModule.getCFNInit(serverName)
                    .getOrAddConfig(CONFIG_SETS, config)
                    .putFile(cfnFile)
                    .putCommand(cfnCommand);
        }
    }

    public CapabilityMapper createCapabilityMapper() {
        return new CapabilityMapper(cfnModule.getAWSRegion(), cfnModule.getAwsCredentials(), logger);
    }
}
