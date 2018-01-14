package org.opentosca.toscana.plugins.cloudformation;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Set;

import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.plugins.scripts.BashScript;
import org.opentosca.toscana.plugins.scripts.EnvironmentCheck;

import org.slf4j.Logger;

/**
 * Class for building scripts needed for deployment of cloudformation templates.
 */
public class CloudFormationScriptCreator {
    private static final String CLI_COMMAND_CREATESTACK = "aws cloudformation deploy ";
    private static final String CLI_PARAM_STACKNAME = "--stack-name ";
    private static final String CLI_PARAM_TEMPLATEFILE = "--template-file ";
    //TODO check if PARAMOVERRIDES are needed and get said parameters
    // private static final String CLI_PARAM_PARAMOVERRIDES = "--parameter-overrides ";
    private static final String FILENAME_DEPLOY = "deploy";
    private static final String FILENAME_UPLOAD = "file-upload";
    private static final String TEMPLATE_PATH = "template.yaml ";
    private static final String CHANGE_TO_PARENT_DIRECTORY = "cd ..";
    
    private final String stackName;
    private final String bucketName;
    private final Hashtable<String, String> filesToBeUploaded;
    private final PluginFileAccess fileAccess;
    private final Logger logger;

    /**
     * Creates a <tt>CloudFormationScriptCreator<tt> in order to build a deployment scripts.
     *
     * @param fileAccess        access to the file system to write scripts
     * @param stackName         name of the stack to be created
     * @param bucketName        where the files will be stored
     * @param filesToBeUploaded all files that need to be uploaded
     * @param logger            standard logger
     */
    public CloudFormationScriptCreator(PluginFileAccess fileAccess, String stackName, String bucketName,
                                       Hashtable<String, String> filesToBeUploaded, Logger logger) {
        this.fileAccess = fileAccess;
        this.stackName = stackName;
        this.bucketName = bucketName;
        this.filesToBeUploaded = filesToBeUploaded;
        this.logger = logger;
    }

    /**
     * Creates all Scripts necessary for AWS CloudFormation deployment.
     */
    public void createScripts() throws IOException {
        createFileUploadScript();
        createDeployScript();
    }

    /**
     * Creates a deploy script for deploying the cloudformation template.
     */
    private void createDeployScript() throws IOException {
        //Create Deployment script for the template
        // TODO maybe add the execution of the fileUploadScript
        BashScript deployScript = new BashScript(fileAccess, FILENAME_DEPLOY);
        deployScript.append(EnvironmentCheck.checkEnvironment("aws"));
        deployScript.append(CHANGE_TO_PARENT_DIRECTORY);
        deployScript.append(CLI_COMMAND_CREATESTACK
            + CLI_PARAM_STACKNAME + stackName + " "
            + CLI_PARAM_TEMPLATEFILE + TEMPLATE_PATH);
    }

    /**
     * Creates the script for File Uploads if files need to be uploaded.
     */
    private void createFileUploadScript() throws IOException {
        if (!filesToBeUploaded.isEmpty()) {
            Set<String> objectKeys = filesToBeUploaded.keySet();

            BashScript fileUploadScript = new BashScript(fileAccess, FILENAME_UPLOAD);
            fileUploadScript.append(createBucket());

            // Add upload commands for all files to be uploaded
            for (String objectKey : objectKeys) {
                fileUploadScript.append(uploadFile(objectKey, filesToBeUploaded.get(objectKey)));
            }
        } else {
            logger.info("No files to be uploaded found.");
            logger.info("Skipping creation of file upload script.");
        }
    }

    /**
     * Creates an S3Bucket with the given name.
     * Wraps resources/cloudformation.scripts/create-bucket.sh
     */
    private String createBucket() {
        return "createBucket \"" + bucketName + "\"";
    }

    /**
     * Puts the given file with the given key on the S3Bucket with  the given name.
     * Wraps resources/cloudformation.scripts/upload-file.sh
     */
    private String uploadFile(String objectKey, String filePath) {
        return "uploadFile \"" + bucketName + "\"" + "\"" + objectKey + "\"" + filePath + "\"";
    }
}
