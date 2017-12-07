package org.opentosca.toscana.plugins.cloudformation;

import com.scaleset.cfbuilder.core.Module;
import com.scaleset.cfbuilder.ec2.SecurityGroup;

public class CloudFormationModule extends Module {
    
    // KeyName is an default input value
    private static final String KEYNAME_DESCRIPTION = "Name of an existing EC2 KeyPair to enable SSH access to the instances";
    private static final String KEYNAME_TYPE = "AWS::EC2::KeyPair::KeyName";
    private static final String KEYNAME_CONSTRAINT_DESCRIPTION = "must be the name of an existing EC2 KeyPair.";
    private static final String KEYNAME = "KeyName";
    
    private Object keyName = option(KEYNAME).orElseGet(
        () -> strParam(KEYNAME).type(KEYNAME_TYPE).description(KEYNAME_DESCRIPTION).constraintDescription(KEYNAME_CONSTRAINT_DESCRIPTION));

    private Object keyNameVar = template.ref(KEYNAME);
    
    
    public Object getKeyNameVar(){
        return this.keyNameVar;
    }
}
