package org.opentosca.toscana.plugins.cloudformation.visitor;

import org.opentosca.toscana.model.node.Apache;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.MysqlDatabase;
import org.opentosca.toscana.model.node.MysqlDbms;
import org.opentosca.toscana.model.node.WebApplication;
import org.opentosca.toscana.model.visitor.StrictNodeVisitor;
import org.opentosca.toscana.plugins.cloudformation.CloudFormationModule;

import com.scaleset.cfbuilder.ec2.Instance;

import com.scaleset.cfbuilder.ec2.SecurityGroup;
import org.slf4j.Logger;

public class CloudFormationNodeVisitor implements StrictNodeVisitor {
    
    private final Logger logger;
    
    public CloudFormationNodeVisitor(Logger logger) throws Exception {
        this.logger = logger;
    }
    
    private CloudFormationModule cfnModule = new CloudFormationModule();

    @Override
    public void visit(Compute node) {
        try {
        //default security group for all EC2 Instances opens for port 80 and 22 to the whole internet
        Object cidrIp = "0.0.0.0/0";
        SecurityGroup webServerSecurityGroup = cfnModule.resource(SecurityGroup.class, "WebServerSecurityGroup")
            .groupDescription("Enable ports 80 and 22")
            .ingress(ingress -> ingress.cidrIp(cidrIp), "tcp", 80, 22);
        
            cfnModule.resource(Instance.class, node.getNodeName())
                .keyName(cfnModule.getKeyNameVar())
                .securityGroupIds(webServerSecurityGroup);
        } catch (Exception e){
            logger.error("Error while creating Instance resource");
            e.printStackTrace();
        }
    }

    @Override
    public void visit(Apache node) {
        
    }

    @Override
    public void visit(WebApplication node) {
        
    }

    @Override
    public void visit(MysqlDatabase node) {
        
    }

    @Override
    public void visit(MysqlDbms node) {
        
    }
}
