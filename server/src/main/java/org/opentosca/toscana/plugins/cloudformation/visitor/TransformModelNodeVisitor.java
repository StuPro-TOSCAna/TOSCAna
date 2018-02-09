package org.opentosca.toscana.plugins.cloudformation.visitor;

import java.util.Set;

import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.model.capability.ComputeCapability;
import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.model.node.Apache;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.MysqlDatabase;
import org.opentosca.toscana.model.node.MysqlDbms;
import org.opentosca.toscana.model.node.Nodejs;
import org.opentosca.toscana.model.node.WebApplication;
import org.opentosca.toscana.model.visitor.StrictNodeVisitor;
import org.opentosca.toscana.plugins.cloudformation.CloudFormationModule;
import org.opentosca.toscana.plugins.cloudformation.mapper.CapabilityMapper;
import org.opentosca.toscana.plugins.cloudformation.util.OperationHandler;
import org.opentosca.toscana.plugins.util.TransformationFailureException;

import com.amazonaws.SdkClientException;
import com.scaleset.cfbuilder.ec2.Instance;
import com.scaleset.cfbuilder.ec2.SecurityGroup;
import com.scaleset.cfbuilder.ec2.metadata.CFNCommand;
import com.scaleset.cfbuilder.ec2.metadata.CFNInit;
import com.scaleset.cfbuilder.ec2.metadata.CFNPackage;
import com.scaleset.cfbuilder.rds.DBInstance;

import static org.opentosca.toscana.plugins.cloudformation.CloudFormationLifecycle.toAlphanumerical;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.CONFIG_CREATE;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.CONFIG_SETS;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.CONFIG_START;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.SECURITY_GROUP;

/**
 Class for building a CloudFormation template from an effective model instance via the visitor pattern. Currently only
 supports LAMP-stacks built with Compute, WebApplication, Apache, MySQL, MySQL nodes.
 */
public class TransformModelNodeVisitor extends CloudFormationVisitorExtension implements StrictNodeVisitor {
    private OperationHandler operationHandler;
    
    /**
     Creates a <tt>TransformModelNodeVisitor<tt> in order to build a template with the given
     <tt>CloudFormationModule<tt>.

     @param context   TransformationContext to extract topology and logger
     @param cfnModule Module to build the template model
     */
    public TransformModelNodeVisitor(TransformationContext context, CloudFormationModule cfnModule) {
        super(context, cfnModule);
        this.operationHandler = new OperationHandler(cfnModule, logger);
    }

    @Override
    public void visit(Compute node) {
        try {
            if (cfnModule.checkComputeToEc2(node)) {
                logger.debug("Compute '{}' will be transformed to EC2", node.getEntityName());
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
        // TODO handle sql artifact if present
    }

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
            
            //Add restart apache2 command to the configscript
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
        try {
            //get the compute where the apache this node is hosted on, is hosted on
            Compute compute = getCompute(node);
            String computeName = toAlphanumerical(compute.getEntityName());
            
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
    
    @Override
    public void visit(Nodejs node) {
        try {
            Compute computeHost = getCompute(node);
            String computeHostName = toAlphanumerical(computeHost.getEntityName());

            // TODO install Nodejs on Compute
            
            //handle configure
            operationHandler.handleConfigure(node, computeHostName);
            //handle start
            operationHandler.handleStart(node, computeHostName);
        } catch (Exception e) {
            logger.error("Error while creating Nodejs");
            throw new TransformationFailureException("Failed at Nodejs node " + node.getEntityName(), e);
        }
    }

    public CapabilityMapper createCapabilityMapper() {
        return new CapabilityMapper(cfnModule.getAWSRegion(), cfnModule.getAwsCredentials(), logger);
    }
}
