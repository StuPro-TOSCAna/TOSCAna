package org.opentosca.toscana.plugins.cloudformation.visitor;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.opentosca.toscana.model.capability.ComputeCapability;
import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.model.node.Apache;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.MysqlDatabase;
import org.opentosca.toscana.model.node.MysqlDbms;
import org.opentosca.toscana.model.node.WebApplication;
import org.opentosca.toscana.model.node.WebServer;
import org.opentosca.toscana.model.operation.Operation;
import org.opentosca.toscana.model.operation.OperationVariable;
import org.opentosca.toscana.model.visitor.StrictNodeVisitor;
import org.opentosca.toscana.model.visitor.UnsupportedTypeException;
import org.opentosca.toscana.plugins.cloudformation.CloudFormationModule;

import com.scaleset.cfbuilder.ec2.Instance;
import com.scaleset.cfbuilder.ec2.SecurityGroup;
import com.scaleset.cfbuilder.ec2.metadata.CFNCommand;
import com.scaleset.cfbuilder.ec2.metadata.CFNFile;
import com.scaleset.cfbuilder.ec2.metadata.CFNInit;
import com.scaleset.cfbuilder.ec2.metadata.CFNPackage;
import com.scaleset.cfbuilder.ec2.metadata.Config;
import com.scaleset.cfbuilder.rds.DBInstance;
import org.slf4j.Logger;

import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.CONFIG_CONFIGURE;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.CONFIG_INSTALL;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.CONFIG_SETS;
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
    public CloudFormationNodeVisitor(Logger logger, CloudFormationModule cfnModule) throws Exception {
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
            if (node.host.getFulfillers().size() == 1) {
                MysqlDbms mysqlDbms = node.host.getFulfillers().toArray(new MysqlDbms[1])[0];
                serverName = toAlphanumerical(mysqlDbms.getHost().getCapability().getName().get());
            } else {
                throw new IllegalStateException("More than one fulfiller");
            }
            String dbName = node.getDatabaseName();
            //throw error, take default or generate random?
            String masterUser = checkOrDefault(node.getUser(), "root");
            String masterPassword = checkOrDefault(node.getPassword(), "abcd1234");
            Integer port = checkOrDefault(node.getPort(), 3306);
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
        // Get the host of this DBMS node
        String host = toAlphanumerical(node.getHost().getCapability().getName().get());
        // TODO what to do if there is a configure script
    }

    @Override
    public void visit(Apache node) {
        logger.debug("Visit Apache node " + node.getNodeName() + ".");
        // check if host is available
        ComputeCapability computeCapability = node.getHost().getCapability();
        if (computeCapability.getName().isPresent()) {
            //Hosted on name
            String host = toAlphanumerical(computeCapability.getName().get());

            cfnModule.getCFNInit(host)
                .getOrAddConfig(CONFIG_SETS, CONFIG_INSTALL)
                .putPackage(
                    //TODO apt only if linux
                    new CFNPackage("apt")
                        .addPackage("apache2"));
        }
    }

    @Override
    public void visit(WebApplication node) {
        logger.debug("Visit WebApplication node " + node.getNodeName() + ".");

        //get the name of the server where this node is hosted on
        String serverName;
        if (node.getHost().getFulfillers().size() == 1) {
            WebServer webServer = node.getHost().getFulfillers().toArray(new WebServer[1])[0];
            serverName = toAlphanumerical(webServer.getHost().getCapability().getName().get());
        } else {
            throw new IllegalStateException("More than one or no fulfiller");
        }

        if (node.getStandardLifecycle().getCreate().isPresent()) {
            Operation create = node.getStandardLifecycle().getCreate().get();

            // add dependencies
            for (String dependency : create.getDependencies()) {
                String cfnFilePath = "/home/ubuntu/"; // TODO Check what path is needed
                // dumping content from file
                String cfnFileContent = "!Sub |\n"; //TODO Check when this is needed
                cfnFileContent += "<html>\n" +
                    " <head>\n" +
                    "  <title>Hello World</title>\n" +
                    " </head>\n" +
                    " <body>\n" +
                    " <?php echo '<p>Hello World</p>'; ?>\n" +
                    " </body>\n" +
                    "</html>"; //TODO add content, currently just adds a "Hello World" php app as content.
                String cfnFileMode = "000600"; //TODO Check what mode is needed
                String cfnFileOwner = "www-data"; //TODO Check what Owner is needed
                String cfnFileGroup = "www-data"; //TODO Check what Group is needed

                CFNFile cfnFile = new CFNFile(cfnFilePath + dependency)// remove beginning of dependency?
                    .setContent(cfnFileContent)
                    .setMode(cfnFileMode)
                    .setOwner(cfnFileOwner)
                    .setGroup(cfnFileGroup);

                cfnModule.getCFNInit(serverName)
                    .getOrAddConfig(CONFIG_SETS, CONFIG_INSTALL)
                    .putFile(cfnFile); //put commands, files
            }

            //Add ImplementationArtifact
            String implementationArtifact = create.getImplementationArtifact().get();
            String cfnFilePath = "/home/ubuntu/"; // TODO Check what path is needed from Implementationartifact?

            String cfnFileMode = "000500"; //TODO Check what mode is needed
            String cfnFileOwner = "root"; //TODO Check what Owner is needed
            String cfnFileGroup = "root"; //TODO Check what Group is needed

            try {
                CFNFile cfnFile = new CFNFile(cfnFilePath + implementationArtifact)
                    .setContent(cfnModule.fileAccess.read(implementationArtifact))
                    .setMode(cfnFileMode)
                    .setOwner(cfnFileOwner)
                    .setGroup(cfnFileGroup);
            
            CFNCommand cfnCommand = new CFNCommand(implementationArtifact,
                "/bin/sh " + cfnFilePath + implementationArtifact); //TODO remove beginning of dependency?
            // add inputs to environment, but where to get other needed variables?
            for (OperationVariable input : create.getInputs()) {
                cfnCommand.addEnv(input.getKey(), checkOrDefault(input.getValue(), "")); //TODO add default
            }
            cfnModule.getCFNInit(serverName)
                .getOrAddConfig(CONFIG_SETS, CONFIG_INSTALL)
                .putFile(cfnFile)
                .putCommand(cfnCommand); //put commands

            } catch (IOException e) {
                logger.error("File not found " + implementationArtifact);
                e.printStackTrace();
            }
        }

        if (node.getStandardLifecycle().getConfigure().isPresent()) {
            Operation configure = node.getStandardLifecycle().getConfigure().get();

            // add dependencies
            for (String dependency : configure.getDependencies()) {
                String cfnFilePath = "tmp/"; // TODO Check what path is needed
                // dumping content from file
                String cfnFileContent = "|\n"; //TODO Check when this is needed
                cfnFileContent += "<html>\n" +
                    "#!/bin/bash"; //TODO add content, currently does nothing.
                String cfnFileMode = "000500"; //TODO Check what mode is needed
                String cfnFileOwner = "root"; //TODO Check what Owner is needed
                String cfnFileGroup = "root"; //TODO Check what Group is needed

                CFNFile cfnFile = new CFNFile(cfnFilePath + dependency) // remove beginning of dependency?
                    .setContent(cfnFileContent)
                    .setMode(cfnFileMode)
                    .setOwner(cfnFileOwner)
                    .setGroup(cfnFileGroup);
                
                cfnModule.getCFNInit(serverName)
                    .getOrAddConfig(CONFIG_SETS, CONFIG_INSTALL)
                    .putFile(cfnFile); //put commands, files
            }

            //Add ImplementationArtifact
            String implementationArtifact = configure.getImplementationArtifact().get();
            String cfnFilePath = "/home/ubuntu/"; // TODO Check what path is needed from Implementationartifact?

            String cfnFileMode = "000500"; //TODO Check what mode is needed
            String cfnFileOwner = "root"; //TODO Check what Owner is needed
            String cfnFileGroup = "root"; //TODO Check what Group is needed

            try {
                CFNFile cfnFile = new CFNFile(cfnFilePath + implementationArtifact)
                    .setContent(cfnModule.fileAccess.read(implementationArtifact))
                    .setMode(cfnFileMode)
                    .setOwner(cfnFileOwner)
                    .setGroup(cfnFileGroup);
                
            CFNCommand cfnCommand = new CFNCommand(implementationArtifact,
                "/bin/sh " + cfnFilePath + implementationArtifact); // remove beginning of dependency?
            // add inputs to environment, but where to get other needed variables?
            for (OperationVariable input : configure.getInputs()) {
                cfnCommand.addEnv(input.getKey(), checkOrDefault(input.getValue(), ""));
            }
            cfnModule.getCFNInit(serverName)
                .getOrAddConfig(CONFIG_SETS, CONFIG_INSTALL)
                .putFile(cfnFile)
                .putCommand(cfnCommand); //put commands

            } catch (IOException e) {
                logger.error("File not found " + implementationArtifact);
                e.printStackTrace();
            }
        }
    }

    private String checkOrDefault(Optional<String> optional, String def) {
        return optional.isPresent() ? optional.get() : def;
    }

    private Integer checkOrDefault(Optional<Integer> optional, Integer def) {
        return optional.isPresent() ? optional.get() : def;
    }

    private String toAlphanumerical(String inp) {
        return inp.replaceAll("[^A-Za-z0-9]", "");
    }
}
