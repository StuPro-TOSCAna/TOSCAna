package org.opentosca.toscana.plugins.cloudformation;

import java.io.IOException;

import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.plugins.scripts.BashScript;
import org.opentosca.toscana.plugins.scripts.EnvironmentCheck;

/**
 * Class for creating scripts needed for AWS CloudFormation deployment.
 */
public class CloudFormationScriptCreator {
    private static final String CLI_COMMAND_CREATESTACK = "aws cloudformation deploy ";
    private static final String CLI_PARAM_STACKNAME = "--stack-name ";
    private static final String CLI_PARAM_TEMPLATEFILE = "--template-file ";
    //TODO check if PARAMOVERRIDES are needed and get said parameters
    private static final String CLI_PARAM_PARAMOVERRIDES = "--parameter-overrides ";
    private static final String FILEPRAEFIX_DEPLOY = "deploy-";
    private static final String TEMPLATE_PATH = "template.yaml ";
    private static final String CHANGE_TO_PARENT_DIRECTORY = "cd ..";
    private String stackName;
    
    private final PluginFileAccess fileAccess;

    /**
     * Creates a CloudFormationScriptCreator with the given fileAccess and stackName.
     * 
     * @param fileAccess Access to the file system to write scripts
     * @param stackName Name of the stack to be created
     */
    public CloudFormationScriptCreator(PluginFileAccess fileAccess, String stackName) {
        this.fileAccess = fileAccess;
        this.stackName = stackName;
    }

    /**
     * Creates all Scripts necessary for AWS CloudFormation deployment.
     */
    public void createScripts() throws IOException {
        createDeployScript();
    }

    /**
     * Creates a deploy script that creates a stack with the AWS Cloudformation template.
     */
    private void createDeployScript() throws IOException {
        //Create Deployment script for the template
        BashScript createStackScript = new BashScript(fileAccess, FILEPRAEFIX_DEPLOY + stackName);
        createStackScript.append(EnvironmentCheck.checkEnvironment("aws"));
        createStackScript.append(CHANGE_TO_PARENT_DIRECTORY);
        createStackScript.append(CLI_COMMAND_CREATESTACK 
            + CLI_PARAM_STACKNAME + stackName + " "
            + CLI_PARAM_TEMPLATEFILE + TEMPLATE_PATH);
    }
}
