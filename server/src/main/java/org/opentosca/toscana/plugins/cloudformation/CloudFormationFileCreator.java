package org.opentosca.toscana.plugins.cloudformation;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.plugins.scripts.BashScript;
import org.opentosca.toscana.plugins.scripts.EnvironmentCheck;
import org.opentosca.toscana.plugins.util.TransformationFailureException;

import com.scaleset.cfbuilder.core.Parameter;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

/**
 Class for building scripts and copying files needed for deployment of cloudformation templates.
 */
public class CloudFormationFileCreator {
    public static final String CLI_COMMAND_CREATESTACK = "aws cloudformation deploy ";
    public static final String CLI_PARAM_STACKNAME = "--stack-name ";
    public static final String CLI_PARAM_TEMPLATEFILE = "--template-file ";
    public static final String CLI_PARAM_PARAMOVERRIDES = "--parameter-overrides ";
    public static final String CLI_PARAM_CAPABILITIES = "--capabilities";
    public static final String CAPABILITY_IAM = "CAPABILITY_IAM";
    public static final String FILENAME_DEPLOY = "deploy";
    public static final String FILENAME_UPLOAD = "file-upload";
    public static final String TEMPLATE_YAML = "template.yaml";
    public static final String CHANGE_TO_PARENT_DIRECTORY = "cd ..";
    public static final String RELATIVE_DIRECTORY_PREFIX = "../files/";

    private final Logger logger;
    private CloudFormationModule cfnModule;

    /**
     Creates a <tt>CloudFormationFileCreator<tt> in order to build deployment scripts and copy files.

     @param context   TransformationContext to extract topology and logger
     @param cfnModule Module to get the necessary CloudFormation information
     */
    public CloudFormationFileCreator(TransformationContext context, CloudFormationModule cfnModule) {
        this.logger = context.getLogger(getClass());
        this.cfnModule = cfnModule;
    }

    /**
     Copies all files that need to be uploaded to the target artifact.
     */
    public void copyFiles() {

        List<String> filesToBeUploaded = cfnModule.getFilesToBeUploaded();

        logger.debug("Checking if files need to be copied.");
        if (!filesToBeUploaded.isEmpty()) {
            logger.debug("Files to be copied found. Attempting to copy files to the target artifact.");
            filesToBeUploaded.forEach((filePath) -> {
                String targetPath = CloudFormationModule.FILEPATH_TARGET + filePath;
                try {
                    cfnModule.getFileAccess().copy(filePath, targetPath);
                } catch (IOException e) {
                    throw new TransformationFailureException("Copying of files to the target artifact failed.", e);
                }
            });
        } else {
            logger.debug("No files to be copied found. Skipping copying of files.");
        }
    }

    /**
     Creates all Scripts necessary for AWS CloudFormation deployment.
     */
    public void createScripts() throws IOException {
        createFileUploadScript();
        createDeployScript();
    }

    /**
     Creates a deploy script for deploying the cloudformation template.
     */
    private void createDeployScript() throws IOException {
        logger.debug("Creating deploy script.");
        BashScript deployScript = new BashScript(cfnModule.getFileAccess(), FILENAME_DEPLOY);
        deployScript.append(EnvironmentCheck.checkEnvironment("aws"));
        // Source file-upload script if needed
        List filesToBeUploaded = cfnModule.getFilesToBeUploaded();
        if (!filesToBeUploaded.isEmpty()) {
            deployScript.append("source " + FILENAME_UPLOAD + ".sh");
        }
        deployScript.append(CHANGE_TO_PARENT_DIRECTORY);
        StringBuilder deployCommand = new StringBuilder("");
        deployCommand.append(CLI_COMMAND_CREATESTACK + CLI_PARAM_STACKNAME)
            .append(cfnModule.getStackName()).append(" ")
            .append(CLI_PARAM_TEMPLATEFILE).append(TEMPLATE_YAML);

        // Add IAM capability if needed
        if (!filesToBeUploaded.isEmpty()) {
            deployCommand.append(" " + CLI_PARAM_CAPABILITIES + " " + CAPABILITY_IAM);
        }

        // Add parameters if needed
        Map<String, Parameter> parameters = cfnModule.getParameters();
        if (!parameters.isEmpty()) {
            deployCommand.append(" " + CLI_PARAM_PARAMOVERRIDES);
            for (Map.Entry<String, Parameter> entry : parameters.entrySet()) {
                String id = entry.getKey();
                deployCommand.append(" ").append(id).append("=$").append(id).append("Var");
            }
        }

        deployCommand.append(" &");
        deployScript.append(deployCommand.toString());
    }

    /**
     Creates the script for File Uploads if files need to be uploaded.
     */
    private void createFileUploadScript() throws IOException {
        List<String> filesToBeUploaded = cfnModule.getFilesToBeUploaded();

        logger.debug("Checking if files need to be uploaded.");
        if (!filesToBeUploaded.isEmpty()) {
            logger.debug("Files to be uploaded found. Creating file upload script.");
            BashScript fileUploadScript = new BashScript(cfnModule.getFileAccess(), FILENAME_UPLOAD);
            fileUploadScript.append(createBucket());

            logger.debug("Adding file upload commands.");
            filesToBeUploaded.forEach((filePath) -> {
                String localFilePath = RELATIVE_DIRECTORY_PREFIX + filePath;
                try {
                    fileUploadScript.append(uploadFile(filePath, localFilePath));
                } catch (IOException e) {
                    throw new TransformationFailureException("Failed to add file uploads to the script.", e);
                }
            });
        } else {
            logger.debug("No files to be uploaded found. Skipping creation of file upload script.");
        }
    }

    /**
     Creates an S3Bucket with the given name.
     Wraps resources/cloudformation.scripts/create-bucket.sh
     */
    private String createBucket() {
        return "createBucket " + cfnModule.getBucketName() + " " + cfnModule.getAWSRegion();
    }

    /**
     Puts the given file with the given key on the S3Bucket with  the given name.
     Wraps resources/cloudformation.scripts/upload-file.sh
     */
    private String uploadFile(String objectKey, String filePath) {
        return "uploadFile " + cfnModule.getBucketName() + " \"" + objectKey + "\" \"" + filePath + "\"";
    }

    /**
     Copies all needed cloudformation utility scripts into the target artifact.

     @throws IOException if scripts cannot be found
     */
    public void copyUtilScripts() throws IOException {
        String resourcePath = "/cloudformation/scripts/util/";
        String outputPath = "output/scripts/util/";
        PluginFileAccess fileAccess = cfnModule.getFileAccess();

        //Iterate over all files in the script list
        List<String> utilScripts = IOUtils.readLines(
            getClass().getResourceAsStream(resourcePath + "scripts-list"),
            Charsets.UTF_8
        );

        logger.debug("Copying util scripts to the target artifact.");
        for (String utilScript : utilScripts) {
            if (!utilScript.isEmpty()) {
                //Copy the file into the desired directory
                logger.debug("Adding " + utilScript + " to the target artifact.");
                InputStreamReader input = new InputStreamReader(
                    getClass().getResourceAsStream(resourcePath + utilScript)
                );
                BufferedWriter output = fileAccess.access(outputPath + utilScript);
                IOUtils.copy(input, output);
                input.close();
                output.close();
            }
        }
    }
}
