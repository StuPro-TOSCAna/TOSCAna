package org.opentosca.toscana.plugins.cloudformation.visitor;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.opentosca.toscana.model.capability.ComputeCapability;
import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.model.node.Apache;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.Dbms;
import org.opentosca.toscana.model.node.MysqlDatabase;
import org.opentosca.toscana.model.node.MysqlDbms;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.node.WebApplication;
import org.opentosca.toscana.model.node.WebServer;
import org.opentosca.toscana.model.operation.Operation;
import org.opentosca.toscana.model.operation.OperationVariable;
import org.opentosca.toscana.model.relation.RootRelationship;
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
import org.jgrapht.Graph;
import org.slf4j.Logger;

import static org.opentosca.toscana.plugins.cloudformation.CloudFormationLifecycle.toAlphanumerical;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.ABSOLUTE_FILE_PATH;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.CONFIG_CONFIGURE;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.CONFIG_CREATE;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.CONFIG_SETS;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.CONFIG_START;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.MODE_500;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.MODE_644;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.OWNER_GROUP_ROOT;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.SECURITY_GROUP;

/**
 Class for building a CloudFormation template from an effective model instance via the visitor pattern. Currently only
 supports LAMP-stacks built with Compute, WebApplication, Apache, MySQL, MySQL nodes.
 */
public class CloudFormationNodeVisitor implements StrictNodeVisitor {

    private final Logger logger;
    private CloudFormationModule cfnModule;
    private Graph<RootNode, RootRelationship> topology;

    /**
     Creates a <tt>CloudFormationNodeVisitor<tt> in order to build a template with the given
     <tt>CloudFormationModule<tt>.

     @param logger    Logger for logging visitor behaviour
     @param cfnModule Module to build the template model
     */
    public CloudFormationNodeVisitor(Logger logger, CloudFormationModule cfnModule, Graph<RootNode, RootRelationship> topology) {
        this.logger = logger;
        this.cfnModule = cfnModule;
        this.topology = topology;
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
            throw new TransformationFailureException("Failed at Compute node " + node.getEntityName(), e);
        }
    }

    @Override
    public void visit(MysqlDatabase node) {
        try {
            logger.info("Visit MysqlDatabase node {}.", node.getEntityName());
            String nodeName = toAlphanumerical(node.getEntityName());

            //get the name of the server where the dbms this node is hosted on, is hosted on
            Dbms dbms = node.getHost().getNode().orElseThrow(
                () -> new IllegalStateException("MysqlDatabase is missing Dbms")
            );
            Compute compute = dbms.getHost().getNode().orElseThrow(
                () -> new IllegalStateException("Dbms is missing Compute")
            );
            String serverName = toAlphanumerical(compute.getEntityName());
            ComputeCapability hostedOnComputeCapability = compute.getHost();
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
            SecurityGroup securityGroup = cfnModule.resource(SecurityGroup.class, securityGroupName)
                .groupDescription("Open database " + dbName + " for access to group " + serverName + SECURITY_GROUP);
            Set<Compute> hostsOfConnectedTo = getHostsOfConnectedTo(node);
            for (Compute hostOfConnectedTo : hostsOfConnectedTo) {
                securityGroup.ingress(ingress -> ingress.sourceSecurityGroupName(
                    cfnModule.ref(toAlphanumerical(hostOfConnectedTo.getEntityName()) + SECURITY_GROUP)),
                    "tcp", 
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
        } catch (Exception e) {
            logger.error("Error while creating DBInstance resource.");
            throw new TransformationFailureException("Failed at MysqlDatabase node " + node.getEntityName(), e);
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
            Compute compute = node.getHost().getNode().orElseThrow(
                () -> new IllegalStateException("Apache is missing compute")
            );
            String computeName = toAlphanumerical(compute.getEntityName());
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
            throw new TransformationFailureException("Failed at Apache node " + node.getEntityName(), e);
        }
    }

    @Override
    public void visit(WebApplication node) {
        logger.info("Visit WebApplication node {}.", node.getEntityName());
        try {
            //get the name of the server where this node is hosted on
            WebServer webServer = node.getHost().getNode().orElseThrow(
                () -> new IllegalStateException("WebApplication is missing WebServer")
            );
            Compute compute = webServer.getHost().getNode().orElseThrow(
                () -> new IllegalStateException("WebServer is missing Compute")
            );
            String computeName = toAlphanumerical(compute.getEntityName());

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
            throw new TransformationFailureException("Failed at WebApplication node " + node.getEntityName(), e);
        }
    }

    private void handleOperation(Operation operation, String serverName, String config) {
        //Add dependencies
        for (String dependency : operation.getDependencies()) {
            String cfnSource = getFileURL(cfnModule.getBucketName(), dependency);

            logger.debug("Marking " + dependency + " as file to be uploaded.");
            cfnModule.putFileToBeUploaded(dependency);

            CFNFile cfnFile = new CFNFile(ABSOLUTE_FILE_PATH + dependency)
                .setSource(cfnSource)
                .setMode(MODE_644) //TODO Check what mode is needed (only read?)
                .setOwner(OWNER_GROUP_ROOT) //TODO Check what Owner is needed
                .setGroup(OWNER_GROUP_ROOT); //TODO Check what Group is needed

            // Add file to config
            cfnModule.getCFNInit(serverName)
                .getOrAddConfig(CONFIG_SETS, config)
                .putFile(cfnFile);
        }

        //Add artifact
        if (operation.getArtifact().isPresent()) {
            String artifact = operation.getArtifact().get().getFilePath();
            String cfnSource = getFileURL(cfnModule.getBucketName(), artifact);

            logger.debug("Marking " + artifact + " as file to be uploaded.");
            cfnModule.putFileToBeUploaded(artifact);

            CFNFile cfnFile = new CFNFile(ABSOLUTE_FILE_PATH + artifact)
                .setSource(cfnSource)
                .setMode(MODE_500) //TODO Check what mode is needed (read? + execute?)
                .setOwner(OWNER_GROUP_ROOT) //TODO Check what Owner is needed
                .setGroup(OWNER_GROUP_ROOT); //TODO Check what Group is needed

            CFNCommand cfnCommand = new CFNCommand(artifact,
                ABSOLUTE_FILE_PATH + artifact) //file is the full path, so need for "./"
                .setCwd(ABSOLUTE_FILE_PATH + new File(artifact).getParent());
            // add inputs to environment
            for (OperationVariable input : operation.getInputs()) {
                Object value = input.getValue().orElseThrow(
                    () -> new IllegalArgumentException("Input value of " + input.getKey() + " expected to not be " +
                        "null")
                );
                cfnCommand.addEnv(input.getKey(), value);
            }
            cfnModule.getCFNInit(serverName)
                .getOrAddConfig(CONFIG_SETS, config)
                .putFile(cfnFile)
                .putCommand(cfnCommand);
        }
    }

    /**
     Returns the URL to the file in the given S3Bucket.
     e.g. http://bucketName.s3.amazonaws.com/objectKey

     @param bucketName name of the bucket containing the file
     @param objectKey  key belonging to the file in the bucket
     @return URL for the file
     */
    private String getFileURL(String bucketName, String objectKey) {
        return CloudFormationModule.URL_HTTP + bucketName + CloudFormationModule.URL_S3_AMAZONAWS + "/" + objectKey;
    }

    public CapabilityMapper createCapabilityMapper() {
        return new CapabilityMapper(cfnModule.getAWSRegion(), cfnModule.getAwsCredentials(), logger);
    }

    private Set<Compute> getHostsOfConnectedTo(RootNode node) {
        Set<Compute> connected = new HashSet<>();
        Set<RootRelationship> incomingEdges = topology.incomingEdgesOf(node);
        for (RootRelationship incomingEdge : incomingEdges) {
            RootNode source = topology.getEdgeSource(incomingEdge);
            if (source instanceof WebApplication) {
                WebApplication webApplication = (WebApplication) source; 
                Compute compute = getCompute(webApplication);
                connected.add(compute);
            }
        }
        return connected;
    }
    
    private Compute getCompute(WebApplication webApplication) {
        WebServer webServer = webApplication.getHost().getNode().orElseThrow(
            () -> new IllegalStateException("WebApplication is missing WebServer")
        );
        return webServer.getHost().getNode().orElseThrow(
            () -> new IllegalStateException("WebServer is missing Compute")
        );
    }
}
