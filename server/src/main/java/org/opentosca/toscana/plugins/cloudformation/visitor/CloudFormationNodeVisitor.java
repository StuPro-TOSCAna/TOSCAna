package org.opentosca.toscana.plugins.cloudformation.visitor;

import java.io.File;

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
import org.opentosca.toscana.model.visitor.UnsupportedTypeException;
import org.opentosca.toscana.plugins.cloudformation.CloudFormationModule;

import com.scaleset.cfbuilder.ec2.Instance;
import com.scaleset.cfbuilder.ec2.SecurityGroup;
import com.scaleset.cfbuilder.ec2.metadata.CFNCommand;
import com.scaleset.cfbuilder.ec2.metadata.CFNFile;
import com.scaleset.cfbuilder.ec2.metadata.CFNInit;
import com.scaleset.cfbuilder.ec2.metadata.CFNPackage;
import com.scaleset.cfbuilder.rds.DBInstance;
import org.slf4j.Logger;

import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.CONFIG_CONFIGURE;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.CONFIG_INSTALL;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.CONFIG_SETS;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.SECURITY_GROUP;

/**
 * Class for building a CloudFormation template from an effective model instance via the visitor pattern. Currently only
 * supports LAMP-stacks built with Compute, WebApplication, Apache, MySQL, MySQL nodes.
 */
public class CloudFormationNodeVisitor implements StrictNodeVisitor {

    private final Logger logger;
    private CloudFormationModule cfnModule;

    /**
     * Creates a <tt>CloudFormationNodeVisitor<tt> in order to build a template with the given
     * <tt>CloudFormationModule<tt>.
     *
     * @param logger    Logger for logging visitor behaviour
     * @param cfnModule Module to build the template model
     */
    public CloudFormationNodeVisitor(Logger logger, CloudFormationModule cfnModule) {
        this.logger = logger;
        this.cfnModule = cfnModule;
    }

    @Override
    public void visit(Compute node) {
        try {
            logger.debug("Visit Compute node " + node.getNodeName() + ".");
            String nodeName = toAlphanumerical(node.getNodeName());
            //default security group the EC2 Instance opens for port 80 and 22 to the whole internet
            Object cidrIp = "0.0.0.0/0";
            SecurityGroup webServerSecurityGroup = cfnModule.resource(SecurityGroup.class,
                nodeName + SECURITY_GROUP)
                .groupDescription("Enable ports 80 and 22")
                .ingress(ingress -> ingress.cidrIp(cidrIp), "tcp", 80, 22);

            // check what image id should be taken
            // we only support linux ubuntu 16.04 but there should be a mapping of os properties to imageIds
            // ImageIds different depending on the region you use this is us-west-2 atm
            OsCapability computeOs = node.getOs();
            String imageId;
            //here should be a check for isPresent, but what to do if not present?
            if (computeOs.getType().get().equals(OsCapability.Type.LINUX) &&
                computeOs.getDistribution().get().equals(OsCapability.Distribution.UBUNTU) &&
                computeOs.getVersion().get().equals("16.04")) {
                imageId = "ami-0def3275";
            } else {
                throw new UnsupportedTypeException("Only Linux, Ubuntu 16.04 supported.");
            }
            //check what host should be taken
            // we only support t2.micro atm since its free for student accounts
            ComputeCapability computeCompute = node.getHost();
            String instanceType;
            //here should be a check for isPresent, but what to do if not present?
            if (computeCompute.getNumCpus().get().equals(1) &&
                computeCompute.getMemSizeInMB().get().equals(1024)) {
                instanceType = "t2.micro";
            } else {
                throw new UnsupportedTypeException("Only 1 CPU and 1024 MB memory supported.");
            }
            //create CFN init and store it
            CFNInit init = new CFNInit(CONFIG_SETS);
            cfnModule.putCFNInit(nodeName, init);
            //takes default 8GB storage but volume
            cfnModule.resource(Instance.class, nodeName)
                .keyName(cfnModule.getKeyNameVar())
                .securityGroupIds(webServerSecurityGroup)
                .imageId(imageId)
                .instanceType(instanceType);
        } catch (Exception e) {
            logger.error("Error while creating EC2Instance resource.");
            e.printStackTrace();
        }
    }

    @Override
    public void visit(MysqlDatabase node) {
        try {
            logger.debug("Visit MysqlDatabase node " + node.getNodeName() + ".");
            String nodeName = toAlphanumerical(node.getNodeName());

            //get the name of the server where the dbms this node is hosted on, is hosted on
            String serverName;
            if (exactlyOneFulfiller(node.host)) {
                Dbms dbms = node.host.getFulfillers().iterator().next();
                if (exactlyOneFulfiller(dbms.getHost())) {
                    Compute compute = dbms.getHost().getFulfillers().iterator().next();
                    serverName = toAlphanumerical(compute.getNodeName());
                } else {
                    throw new IllegalStateException("Got " + dbms.getHost().getFulfillers().size() + " instead of one fulfiller");
                }
            } else {
                throw new IllegalStateException("Got " + node.getHost().getFulfillers().size() + " instead of one fulfiller");
            }
            String dbName = node.getDatabaseName();
            //throw error, take default or generate random?
            String masterUser = node.getUser().orElseThrow(() -> new IllegalArgumentException("Database user not set"));
            String masterPassword = node.getPassword().orElseThrow(() -> new IllegalArgumentException("Database password not set"));
            Integer port = node.getPort().orElse(3306);
            //TODO check downwards to compute and take its values
            String dBInstanceClass = "db.t2.micro";
            Integer allocatedStorage = 20;
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
            e.printStackTrace();
        }
    }

    @Override
    public void visit(MysqlDbms node) {
        logger.debug("Visit MysqlDbms node " + node.getNodeName() + ".");
        // TODO handle sql artifact if present
    }

    @Override
    public void visit(Apache node) {
        logger.debug("Visit Apache node " + node.getNodeName() + ".");
        String serverName;
        if (exactlyOneFulfiller(node.getHost())) {
            Compute compute = node.getHost().getFulfillers().iterator().next();
            serverName = toAlphanumerical(compute.getNodeName());
        } else {
            throw new IllegalStateException("Got " + node.getHost().getFulfillers().size() + " instead of one fulfiller");
        }
        cfnModule.getCFNInit(serverName)
            .getOrAddConfig(CONFIG_SETS, CONFIG_INSTALL)
            .putPackage(
                //TODO apt only if linux
                new CFNPackage("apt")
                    .addPackage("apache2"));
        //instead of lifecycle create we add the package apache2 to the configset
        if (node.getStandardLifecycle().getConfigure().isPresent()) {
            Operation configure = node.getStandardLifecycle().getConfigure().get();
            handleOperation(configure, serverName, CONFIG_CONFIGURE);
        }
    }

    @Override
    public void visit(WebApplication node) {
        logger.debug("Visit WebApplication node " + node.getNodeName() + ".");

        //get the name of the server where this node is hosted on
        String serverName;
        if (exactlyOneFulfiller(node.getHost())) {
            WebServer webServer = node.getHost().getFulfillers().iterator().next();
            if (exactlyOneFulfiller(webServer.getHost())) {
                Compute compute = webServer.getHost().getFulfillers().iterator().next();
                serverName = toAlphanumerical(compute.getNodeName());
            } else {
                throw new IllegalStateException("Got " + webServer.getHost().getFulfillers().size() + " instead of one fulfiller");
            }
        } else {
            throw new IllegalStateException("Got " + node.getHost().getFulfillers().size() + " instead of one fulfiller");
        }

        if (node.getStandardLifecycle().getCreate().isPresent()) {
            Operation create = node.getStandardLifecycle().getCreate().get();
            handleOperation(create, serverName, CONFIG_INSTALL);
        }

        if (node.getStandardLifecycle().getConfigure().isPresent()) {
            Operation configure = node.getStandardLifecycle().getConfigure().get();
            handleOperation(configure, serverName, CONFIG_CONFIGURE);
        }
    }

    private String toAlphanumerical(String inp) {
        return inp.replaceAll("[^A-Za-z0-9]", "");
    }

    private boolean exactlyOneFulfiller(Requirement requirement) {
        return (requirement.getFulfillers().size() == 1);
    }

    private void handleOperation(Operation operation, String serverName, String config) {
        String cfnFilePath = "/home/ubuntu/"; // TODO Check what path is needed

        //Add dependencies
        for (String dependency : operation.getDependencies()) {
            String cfnFileMode = "000644"; //TODO Check what mode is needed (only read?)
            String cfnFileOwner = "root"; //TODO Check what Owner is needed
            String cfnFileGroup = "root"; //TODO Check what Group is needed
            String cfnSource = getFileURL(cfnModule.getBucketName(), dependency);
            
            logger.debug("Marking " + dependency + " as file to be uploaded.");
            cfnModule.putFileToBeUploaded(dependency, dependency);
            
            CFNFile cfnFile = new CFNFile(cfnFilePath + dependency)
                .setSource(cfnSource)
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
            String cfnSource = getFileURL(cfnModule.getBucketName(), artifact);
            
            logger.debug("Marking " + artifact + " as file to be uploaded.");
            cfnModule.putFileToBeUploaded(artifact, artifact);
            
            CFNFile cfnFile = new CFNFile(cfnFilePath + artifact)
                .setSource(cfnSource)
                .setMode(cfnFileMode)
                .setOwner(cfnFileOwner)
                .setGroup(cfnFileGroup);

            CFNCommand cfnCommand = new CFNCommand(artifact,
                cfnFilePath + artifact) //file is the full path, so need for "./"
                .setCwd(cfnFilePath + new File(artifact).getParent());
            // add inputs to environment, but where to get other needed variables?
            for (OperationVariable input : operation.getInputs()) {
                Object value = input.getValue().orElse("");
                if (("127.0.0.1".equals(value) || "localhost".equals(value)) && input.getKey().contains("host")) {
                    value = cfnModule.fnGetAtt("mydb", "Endpoint.Address");
                }
                cfnCommand.addEnv(input.getKey(), value); //TODO add default
            }
            cfnModule.getCFNInit(serverName)
                .getOrAddConfig(CONFIG_SETS, config)
                .putFile(cfnFile)
                .putCommand(cfnCommand)
                .putCommand(new CFNCommand("restart apache2", "service apache2 restart")); //put commands
        }
    }

    /**
     * Returns the URL to the file in the given S3Bucket.
     * e.g. http://bucketName.s3.amazonaws.com/objectKey
     *
     * @param bucketName name of the bucket containing the file
     * @param objectKey  key belonging to the file in the bucket
     * @return URL for the file
     */
    private String getFileURL(String bucketName, String objectKey) {
        return CloudFormationModule.URL_HTTP + bucketName + CloudFormationModule.URL_S3_AMAZONAWS + "/" + objectKey;
    }
}
