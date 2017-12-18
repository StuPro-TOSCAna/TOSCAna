package org.opentosca.toscana.plugins.cloudformation;

import java.io.IOException;

import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.plugins.scripts.BashScript;
import org.opentosca.toscana.plugins.scripts.EnvironmentCheck;

public class CloudFormationScriptCreator {
    private static final String CLI_COMMAND_CREATESTACK = "aws cloudformation deploy ";
    private static final String CLI_PARAM_STACKNAME = "--stack-name ";
    private static final String CLI_PARAM_TEMPLATEURL = "--template-file ";
    private static final String FILEPRAEFIX_DEPLOY = "deploy-";
    private static final String TEMPLATE_URL = "${template_url}"; //TODO Handle setting Template URL
    private static final String PARAMETERS = "parameter-overrides";
    private String stackName;
    
    private final PluginFileAccess fileAccess;
    
    public CloudFormationScriptCreator(PluginFileAccess fileAccess, String stackName){
        this.fileAccess = fileAccess;
        this.stackName = stackName;
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
        BashScript createStackScript = new BashScript(fileAccess, FILEPRAEFIX_DEPLOY + stackName);
        createStackScript.append(EnvironmentCheck.checkEnvironment("aws"));
        createStackScript.append(CLI_COMMAND_CREATESTACK 
            + CLI_PARAM_STACKNAME + stackName + " "
            + CLI_PARAM_TEMPLATEURL + TEMPLATE_URL
            + PARAMETERS + "");
    }
}
