package org.opentosca.toscana.plugins.cloudformation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opentosca.toscana.core.plugin.PluginFileAccess;

import com.scaleset.cfbuilder.core.Fn;
import com.scaleset.cfbuilder.core.Module;
import com.scaleset.cfbuilder.core.Resource;
import com.scaleset.cfbuilder.core.Template;
import com.scaleset.cfbuilder.ec2.Instance;
import com.scaleset.cfbuilder.ec2.UserData;
import com.scaleset.cfbuilder.ec2.metadata.CFNInit;

public class CloudFormationModule extends Module {

    public final static String CONFIG_SETS = "InstallAndConfigure";
    public final static String CONFIG_INSTALL = "Install";
    public final static String CONFIG_CONFIGURE = "Configure";
    public final static String SECURITY_GROUP = "SecurityGroup";
    // KeyName is a default input value
    private static final String KEYNAME_DESCRIPTION = "Name of an existing EC2 KeyPair to enable SSH access to the instances";
    private static final String KEYNAME_TYPE = "AWS::EC2::KeyPair::KeyName";
    private static final String KEYNAME_CONSTRAINT_DESCRIPTION = "must be the name of an existing EC2 KeyPair.";
    private static final String KEYNAME = "KeyName";
    private static final String USERDATA_NAME = "Join";
    private static final String USERDATA_DELIMITER = "";
    private static final String[] USERDATA_CONSTANT_PARAMS = {
        "#!/bin/bash -xe\n",
        "mkdir -p /tmp/aws-cfn-bootstrap-latest\n",
        "curl https://s3.amazonaws.com/cloudformation-examples/aws-cfn-bootstrap-latest.tar.gz | tar xz -C /tmp/aws-cfn-bootstrap-latest --strip-components 1\n",
        "apt-get update\n",
        "DEBIAN_FRONTEND=noninteractive apt-get upgrade -yq\n",
        "apt-get -y install python-setuptools\n",
        "easy_install /tmp/aws-cfn-bootstrap-latest\n",
        "cp /tmp/aws-cfn-bootstrap-latest/init/ubuntu/cfn-hup /etc/init.d/cfn-hup\n",
        "chmod 755 /etc/init.d/cfn-hup\n",
        "update-rc.d cfn-hup defaults\n",
        "# Install the files and packages from the metadata\n",
        "/usr/local/bin/cfn-init -v ",
        "         --stack "};

    private Object keyNameVar;

    private Map<String, CFNInit> cfnInitMap;

    private PluginFileAccess fileAccess;

    /**
     Create a Module which uses the cloudformation-builder to build an AWS CloudFormation template

     @param fileAccess fileAccess to append the content of files to the template
     */
    public CloudFormationModule(PluginFileAccess fileAccess) {
        this.id("").template(new Template());
        strParam(KEYNAME).type(KEYNAME_TYPE).description(KEYNAME_DESCRIPTION).constraintDescription(KEYNAME_CONSTRAINT_DESCRIPTION);
        keyNameVar = template.ref(KEYNAME);
        cfnInitMap = new HashMap<>();
        this.fileAccess = fileAccess;
    }

    /**
     Put a CFNInit into a map which will be added to the resource at build time

     @param resource resource to add CFNInit to
     @param init     CNFInit to add
     */
    public void putCFNInit(String resource, CFNInit init) {
        cfnInitMap.put(resource, init);
    }

    /**
     Get the CFNInit which belongs to a specific resource

     @param resource String id of a resource
     */
    public CFNInit getCFNInit(String resource) {
        return this.cfnInitMap.get(resource);
    }

    /**
     Get a ref to the KeyName of this template
     */
    public Object getKeyNameVar() {
        return this.keyNameVar;
    }

    private Fn getUserDataFn(String resource, String configsets) {
        // Initialise params that need refs
        Object[] userdataRefParams = {
            template.ref("AWS::StackName"),
            "         --resource " + resource + " ",
            "         --configsets " + configsets + " ",
            "         --region ",
            template.ref("AWS::Region"),
            "\n",
            "# Signal the status from cfn-init\n",
            "/usr/local/bin/cfn-signal -e $? ",
            "         --stack ",
            template.ref("AWS::StackName"),
            "         --resource " + resource + " ",
            "         --region ",
            template.ref("AWS::Region"),
            "\n"};

        // Combine constant params with ref params
        List params = new ArrayList<Object>();
        Collections.addAll(params, USERDATA_CONSTANT_PARAMS);
        Collections.addAll(params, userdataRefParams);

        return Fn.fnDelimiter(USERDATA_NAME, USERDATA_DELIMITER, params.toArray());
    }

    @Override
    public String toString() {
        try {
            this.build();
            return this.template.toString(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     Get the fileAccess of this module
     */
    public PluginFileAccess getFileAccess() {
        return fileAccess;
    }

    /**
     Build the template
     1. Add CFNInit to corresponding instance resource
     */
    public void build() {
        for (Map.Entry<String, CFNInit> pair : cfnInitMap.entrySet()) {
            Resource res = this.getResource(pair.getKey());
            if (res instanceof Instance) {
                Instance instance = (Instance) res;
                instance
                    .addCFNInit(pair.getValue())
                    .userData(new UserData(getUserDataFn(pair.getKey(), CONFIG_SETS)));
            }
        }
    }
}
