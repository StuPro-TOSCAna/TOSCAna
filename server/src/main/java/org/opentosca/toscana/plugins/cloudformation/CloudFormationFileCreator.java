package org.opentosca.toscana.plugins.cloudformation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.plugins.scripts.BashScript;
import org.opentosca.toscana.plugins.scripts.EnvironmentCheck;

import org.slf4j.Logger;

/**
 * Class for building scripts and copying files needed for deployment of cloudformation templates.
 */
public class CloudFormationFileCreator {
    private static final String CLI_COMMAND_CREATESTACK = "aws cloudformation deploy ";
    private static final String CLI_PARAM_STACKNAME = "--stack-name ";
    private static final String CLI_PARAM_TEMPLATEFILE = "--template-file ";
    //TODO check if PARAMOVERRIDES are needed and get said parameters
    // private static final String CLI_PARAM_PARAMOVERRIDES = "--parameter-overrides ";
    private static final String FILENAME_DEPLOY = "deploy";
    private static final String FILENAME_UPLOAD = "file-upload";
    private static final String TEMPLATE_PATH = "template.yaml ";
    private static final String CHANGE_TO_PARENT_DIRECTORY = "cd ..";

    private final Logger logger;
    private CloudFormationModule cfnModule;

    /**
     * Creates a <tt>CloudFormationFileCreator<tt> in order to build deployment scripts and copy files.
     *
     * @param cfnModule Module to get the necessary CloudFormation information
     * @param logger    standard logger
     */
    public CloudFormationFileCreator(Logger logger, CloudFormationModule cfnModule) {
        this.logger = logger;
        this.cfnModule = cfnModule;
    }

    /**
     * Copies all files that need to be uploaded to the target artifact.
     */
    private void copyFiles() throws IOException {
        for (String filePath : cfnModule.getFilesToBeUploaded().values()) {
            cfnModule.getFileAccess().copy(filePath);
        }
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
        BashScript deployScript = new BashScript(cfnModule.getFileAccess(), FILENAME_DEPLOY);
        deployScript.append(EnvironmentCheck.checkEnvironment("aws"));
        deployScript.append(CHANGE_TO_PARENT_DIRECTORY);
        deployScript.append(CLI_COMMAND_CREATESTACK
            + CLI_PARAM_STACKNAME + cfnModule.getStackName() + " "
            + CLI_PARAM_TEMPLATEFILE + TEMPLATE_PATH);
    }

    /**
     * Creates the script for File Uploads if files need to be uploaded.
     */
    private void createFileUploadScript() throws IOException {
        Map<String, String> filesToBeUploaded = cfnModule.getFilesToBeUploaded();

        // Check if files need to be uploaded
        if (!filesToBeUploaded.isEmpty()) {
            // Add create Bucket command
            BashScript fileUploadScript = new BashScript(cfnModule.getFileAccess(), FILENAME_UPLOAD);
            fileUploadScript.append(createBucket());

            // Add upload commands for all files to be uploaded
            Iterator uploadIterator = filesToBeUploaded.entrySet().iterator();
            while (uploadIterator.hasNext()) {
                Map.Entry fileToBeUploaded = (Map.Entry) uploadIterator.next();
                String objectKey = (String) fileToBeUploaded.getKey();
                String filePath = (String) fileToBeUploaded.getValue();
                fileUploadScript.append(uploadFile(objectKey, filePath));
                uploadIterator.remove();
            }
        } else {
            logger.debug("No files to be uploaded found.");
            logger.debug("Skipping creation of file upload script.");
        }
    }

    /**
     * Creates an S3Bucket with the given name.
     * Wraps resources/cloudformation.scripts/create-bucket.sh
     */
    private String createBucket() {
        return "createBucket \"" + cfnModule.getBucketName() + "\"";
    }

    /**
     * Puts the given file with the given key on the S3Bucket with  the given name.
     * Wraps resources/cloudformation.scripts/upload-file.sh
     */
    private String uploadFile(String objectKey, String filePath) {
        return "uploadFile \"" + cfnModule.getBucketName() + "\"" + "\"" + objectKey + "\"" + filePath + "\"";
    }
}
