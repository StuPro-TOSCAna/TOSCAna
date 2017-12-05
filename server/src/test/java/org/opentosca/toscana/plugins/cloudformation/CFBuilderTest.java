package org.opentosca.toscana.plugins.cloudformation;

import com.scaleset.cfbuilder.core.Module;
import com.scaleset.cfbuilder.core.Parameter;
import com.scaleset.cfbuilder.core.Template;
import com.scaleset.cfbuilder.ec2.Instance;
import com.scaleset.cfbuilder.ec2.SecurityGroup;
import com.scaleset.cfbuilder.ec2.SecurityGroupIngress;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class CFBuilderTest extends Module {
    
    @Test
    public void simpleTest() throws Exception {
        Template lampTemplate = new Template();
//        Module testModule = new Module();
        
        // Build Resources
//        testModule.resource(WebServerInstance.class, "WebServerInstance")
//            .name
//        testModule.resource(WebServerInstance.class, "WebServerInstance").na;
        // Set Parameters
//        lampTemplate.strParam("KeyName").description("Name of an existing EC2 KeyPair to enable SSH access to the instances").type("AWS::EC2::KeyPair::KeyName").constraintDescription("must be the name of an existing EC2 KeyPair.");
//        // Set Resources
//        lampTemplate.resource(WebServerInstance.class, ns("WebServerInstance"));
        
        
        new CFBuilderTest.TestModule().id("").template(lampTemplate).build();

        assertNotNull(lampTemplate);
        System.err.println(lampTemplate.toString());
        
//        ObjectMapper mapper = new ObjectMapper().registerModule(new CloudFormationJsonModule().scanTypes());
//        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);
//
//        Template template = mapper.readValue(lampTemplate.toString(), Template.class);
//        assertNotNull(template);
//        System.err.println(template.toString());
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
