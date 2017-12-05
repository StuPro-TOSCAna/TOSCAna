package org.opentosca.toscana.plugins.cloudformation;

import com.scaleset.cfbuilder.core.Module;
import com.scaleset.cfbuilder.core.Parameter;
import com.scaleset.cfbuilder.core.Template;
import com.scaleset.cfbuilder.ec2.Instance;
import com.scaleset.cfbuilder.ec2.SecurityGroup;
import com.scaleset.cfbuilder.ec2.SecurityGroupIngress;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class CloudFormationBuilderTest extends Module {
    
    @Test
    public void simpleTest() throws Exception {
        Template lampTemplate = new Template();
        
        new CloudFormationBuilderTest.TestModule().id("").template(lampTemplate).build();

        assertNotNull(lampTemplate);
        System.err.println(lampTemplate.toString());
    }

    class TestModule extends Module {
        
        public void build() throws Exception {
            
            Object keyName = option("KeyName").orElseGet(
                () -> strParam("KeyName").type("AWS::EC2::KeyPair::KeyName").description("Name of an existing EC2 KeyPair to enable SSH access to the instances").constraintDescription("must be the name of an existing EC2 KeyPair."));

            Parameter imageId = (Parameter) option("ImageId").orElseGet(() -> strParam("ImageId").description("An existing ImageId"));
            Object clusterName = option("clusterName").orElse("elasticsearch");
            Object cidrIp = "0.0.0.0/0";
            Object keyNameVar = template.ref("KeyName");
            Object imageIdVar = template.ref("ImageId");
            Object az = template.ref("MyAZ");
            Object instanceProfile = ref("InstanceProfile");
            Object vpcId = template.ref("VpcId");

            SecurityGroup webServerSecurityGroup = resource(SecurityGroup.class, "WebServerSecurityGroup")
                .ingress(ingress -> ingress.cidrIp(cidrIp), "tcp", 80, 22);

            Object groupId = webServerSecurityGroup.fnGetAtt("GroupId");

            resource(SecurityGroupIngress.class, "SelfReferenceIngress")
                .sourceSecurityGroupId(groupId)
                .groupId(groupId)
                .ipProtocol("tcp")
                .port(9300);

            resource(Instance.class, "Instance")
                .name(ns("Instance"))
                .availabilityZone(az)
                .keyName(keyNameVar)
                .imageId(imageIdVar)
                .instanceProfile(instanceProfile)
                .instanceType(keyName)
                .securityGroupIds(webServerSecurityGroup);
            
            resource(Instance.class, "WebServerInstance")
                .imageId("ami-0def3275")
                .instanceType("t2.micro")
                .securityGroupIds(webServerSecurityGroup)
                .keyName(keyName);
            
            Object value = option("Value");
            
            output("websiteURL", value, "URL for newly created LAMP stack");
        }
    }
}
