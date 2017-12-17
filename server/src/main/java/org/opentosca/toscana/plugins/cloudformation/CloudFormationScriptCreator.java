package org.opentosca.toscana.plugins.cloudformation;

import java.io.IOException;

import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.plugins.scripts.BashScript;
import org.opentosca.toscana.plugins.scripts.EnvironmentCheck;

public class CloudFormationScriptCreator {
    private static final String CLI_COMMAND_CREATESTACK = "aws cloudformation create-stack ";
    private static final String CLI_PARAM_STACKNAME = "--stack-name ";
    private static final String CLI_PARAM_TEMPLATEURL = "--template-url ";
    private static final String FILEPRAEFIX_DEPLOY = "deploy-";
    private static final String TEMPLATE_URL = "${template_url}"; //TODO Handle setting Template URL
    private static final String STACK_NAME = "stack-name"; //TODO get stack name, maybe from cfnModule?
    
    private final PluginFileAccess fileAccess;
    
    public CloudFormationScriptCreator(PluginFileAccess fileAccess){
        this.fileAccess = fileAccess;
    }

    /**
     * Creates all Scripts necessary for CloudFormation deployment.
     */
    public void createScripts() throws IOException {
        createDeployScript();
    }

    /**
     * Creates a deploy script that create a stack with the cloudformation template.
     */
    private void createDeployScript() throws IOException {
        //Create Deployment script for the template
        BashScript createStackScript = new BashScript(fileAccess, FILEPRAEFIX_DEPLOY + STACK_NAME);
        createStackScript.append(EnvironmentCheck.checkEnvironment("aws"));
        createStackScript.append(CLI_COMMAND_CREATESTACK 
            + CLI_PARAM_STACKNAME + STACK_NAME + " "
            + CLI_PARAM_TEMPLATEURL + TEMPLATE_URL);
    }
}
