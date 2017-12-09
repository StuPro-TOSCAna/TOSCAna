package org.opentosca.toscana.plugins.cloudformation.visitor;

import java.util.Optional;

import org.opentosca.toscana.model.capability.ComputeCapability;
import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.model.node.Apache;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.MysqlDatabase;
import org.opentosca.toscana.model.node.MysqlDbms;
import org.opentosca.toscana.model.node.WebApplication;
import org.opentosca.toscana.model.visitor.StrictNodeVisitor;
import org.opentosca.toscana.model.visitor.UnsupportedTypeException;
import org.opentosca.toscana.plugins.cloudformation.CloudFormationModule;

import com.scaleset.cfbuilder.core.Resource;
import com.scaleset.cfbuilder.ec2.Instance;
import com.scaleset.cfbuilder.ec2.SecurityGroup;
import com.scaleset.cfbuilder.ec2.UserData;
import com.scaleset.cfbuilder.ec2.metadata.CFNInit;
import com.scaleset.cfbuilder.ec2.metadata.CFNPackage;
import com.scaleset.cfbuilder.ec2.metadata.Config;
import com.scaleset.cfbuilder.rds.DBInstance;
import org.slf4j.Logger;

public class CloudFormationNodeVisitor implements StrictNodeVisitor {

    private final static String CONFIG_SETS = "InstallAndConfigure";
    private final static String CONFIG_INSTALL = "Install";
    private final static String CONFIG_CONFIGURE = "Configure";
    private final static String SECURITY_GROUP = "SecurityGroup";
    private final Logger logger;
    private CloudFormationModule cfnModule;

    public CloudFormationNodeVisitor(Logger logger, CloudFormationModule cfnModule) throws Exception {
        this.logger = logger;
        this.cfnModule = cfnModule;
    }

    @Override
    public void visit(Compute node) {
        try {
            logger.debug("Visit compute node " + node.getNodeName());
            String nodeName = toAlphanumerical(node.getNodeName());
            //default security group the EC2 Instance opens for port 80 and 22 to the whole internet
            Object cidrIp = "0.0.0.0/0";
            SecurityGroup webServerSecurityGroup = cfnModule.resource(SecurityGroup.class, nodeName + SECURITY_GROUP)
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
                throw new UnsupportedTypeException("Only Linux, Ubuntu, 16.04 supported");
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
                throw new UnsupportedTypeException("Only 1 Cpu and 1024Mb memory supported");
            }
            //takes default 8GB storage but volume
            cfnModule.resource(Instance.class, nodeName)
                .keyName(cfnModule.getKeyNameVar())
                .securityGroupIds(webServerSecurityGroup)
                .imageId(imageId)
                .instanceType(instanceType);
        } catch (Exception e) {
            logger.error("Error while creating EC2Instance resource");
            e.printStackTrace();
        }
    }

    @Override
    public void visit(MysqlDatabase node) {
        try {
            logger.debug("Visit MysqlDatabase node " + node.getNodeName());
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
                .ingress(ingress -> ingress.sourceSecurityGroupName(cfnModule.ref(serverName + SECURITY_GROUP)), "tcp", port);

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
            logger.error("Error while creating DBInstance resource");
            e.printStackTrace();
        }
    }

    @Override
    public void visit(MysqlDbms node) {
        logger.debug("Visit MysqlDbms node " + node.getNodeName());
        //skip for now but
        //TODO check host, what to do if there is a configure script
    }

    @Override
    public void visit(Apache node) {
        logger.debug("Visit apache node " + node.getNodeName());
        // check if host is available
        ComputeCapability computeCapability = node.getHost().getCapability();
        if (computeCapability.getName().isPresent()) {
            //Hosted on name
            String host = toAlphanumerical(computeCapability.getName().get());
            //check if resource already exists and is a EC2 instance
            Resource hostRes = cfnModule.getResource(host);
            if (hostRes != null && hostRes instanceof Instance) {
                Instance hostInstance = (Instance) hostRes;
                hostInstance
                    .addCFNInit(new CFNInit(CONFIG_SETS)
                        .addConfig(CONFIG_SETS,
                            new Config(CONFIG_INSTALL)
                                .putPackage(
                                    //TODO apt only if linux
                                    new CFNPackage("apt")
                                        .addPackage("apache2"))));
                hostInstance.userData(new UserData(cfnModule.getUserDataFn(host, CONFIG_SETS)));
            } else {
                throw new IllegalStateException("The resource: \"" + host + "\" this Apache is hosted on doesn't exist or isn't a Instance");
            }
        }
    }

    @Override
    public void visit(WebApplication node) {
        //noop
    }

    private String checkOrDefault(Optional<String> optional, String def) {
        return optional.isPresent() ? optional.get() : def;
    }

    private Integer checkOrDefault(Optional<Integer> optional, Integer def) {
        return optional.isPresent() ? optional.get() : def;
    }
    
    private String toAlphanumerical(String inp){
        return inp.replaceAll("[^A-Za-z0-9]", "");
    }
}
