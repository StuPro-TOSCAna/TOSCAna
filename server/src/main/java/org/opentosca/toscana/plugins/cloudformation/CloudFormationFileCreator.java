package org.opentosca.toscana.plugins.cloudformation;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.plugins.cloudformation.util.FileUpload;
import org.opentosca.toscana.plugins.scripts.BashScript;
import org.opentosca.toscana.plugins.scripts.EnvironmentCheck;
import org.opentosca.toscana.plugins.util.TransformationFailureException;

import com.scaleset.cfbuilder.core.Parameter;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import static org.opentosca.toscana.core.plugin.lifecycle.AbstractLifecycle.UTIL_DIR_PATH;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.FILEPATH_TARGET;
import static org.opentosca.toscana.plugins.cloudformation.util.FileUpload.UploadFileType.FROM_CSAR;
import static org.opentosca.toscana.plugins.cloudformation.util.FileUpload.UploadFileType.UTIL;
import static org.opentosca.toscana.plugins.cloudformation.util.FileUpload.getFilePaths;
import static org.opentosca.toscana.plugins.cloudformation.util.FileUpload.getFileUploadByType;

/**
 Class for building scripts and copying files needed for deployment of cloudformation templates.
 */
public class CloudFormationFileCreator {
    public static final String CLI_COMMAND_CREATESTACK = "aws cloudformation deploy ";
    public static final String CLI_COMMAND_DELETESTACK = "aws cloudformation delete-stack ";
    public static final String CLI_COMMAND_DELETEBUCKET = "aws s3 rb s3://";
    public static final String COMMAND_ECHO = "echo ";
    public static final String STRING_DELETESTACK = "delete stack: ";
    public static final String CLI_PARAM_STACKNAME = "--stack-name ";
    public static final String CLI_PARAM_TEMPLATEFILE = "--template-file ";
    public static final String CLI_PARAM_PARAMOVERRIDES = "--parameter-overrides";
    public static final String CLI_PARAM_CAPABILITIES = "--capabilities";
    public static final String CLI_PARAM_BUCKET = "--bucket ";
    public static final String CLI_PARAM_FORCE = "--force";
    public static final String CAPABILITY_IAM = "CAPABILITY_IAM";
    public static final String FILENAME_DEPLOY = "deploy";
    public static final String FILENAME_UPLOAD = "file-upload";
    public static final String FILENAME_CREATE_STACK = "create-stack";
    public static final String FILENAME_CLEANUP = "cleanup";
    public static final String TEMPLATE_YAML = "template.yaml";
    public static final String CHANGE_TO_PARENT_DIRECTORY = "cd ..";
    public static final String RELATIVE_DIRECTORY_PREFIX = "../files/";
    public static final String FILEPATH_CLOUDFORMATION = "/cloudformation/";
    public static final String FILEPATH_SCRIPTS_UTIL = FILEPATH_CLOUDFORMATION + "scripts/util/";
    public static final String FILEPATH_FILES_UTIL = FILEPATH_CLOUDFORMATION + "files/util/";

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
        List<String> fileUploadList = getFilePaths(getFileUploadByType(cfnModule.getFileUploadList(), FROM_CSAR));

        logger.debug("Checking if files need to be copied.");
        if (!fileUploadList.isEmpty()) {
            logger.debug("Files to be copied found. Attempting to copy files to the target artifact.");
            fileUploadList.forEach((filePath) -> {
                String targetPath = FILEPATH_TARGET + filePath;
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
    public void writeScripts() throws IOException {
        writeFileUploadScript();
        writeStackCreationScript();
        writeDeployScript();
        writeCleanUpScript();
    }

    /**
     Creates a deploy script for deploying the cloudformation template.
     */
    private void writeDeployScript() throws IOException {
        logger.debug("Creating deploy script.");
        BashScript deployScript = new BashScript(cfnModule.getFileAccess(), FILENAME_DEPLOY);
        deployScript.append(EnvironmentCheck.checkEnvironment("aws"));
        // Source file-upload script if needed
        List fileUploadList = cfnModule.getFileUploadList();
        if (!fileUploadList.isEmpty()) {
            deployScript.append("source " + FILENAME_UPLOAD + ".sh");
        }
        deployScript.append("source " + FILENAME_CREATE_STACK + ".sh");
    }

    /**
     Creates the script for File Uploads if files need to be uploaded.
     */
    private void writeFileUploadScript() throws IOException {
        List<String> fileUploadList = getFilePaths(cfnModule.getFileUploadList());

        logger.debug("Checking if files need to be uploaded.");
        if (!fileUploadList.isEmpty()) {
            logger.debug("Files to be uploaded found. Creating file upload script.");
            BashScript fileUploadScript = new BashScript(cfnModule.getFileAccess(), FILENAME_UPLOAD);
            fileUploadScript.append(createBucket());

            logger.debug("Adding file upload commands.");
            addFileUploadsToScript(fileUploadList, fileUploadScript);
        } else {
            logger.debug("No files to be uploaded found. Skipping creation of file upload script.");
        }
    }

    /**
     Creates the script for creating the CloudFormation stack from the template.
     */
    private void writeStackCreationScript() throws IOException {
        logger.debug("Creating create-stack script.");
        BashScript createStackScript = new BashScript(cfnModule.getFileAccess(), FILENAME_CREATE_STACK);

        // Build deploy command
        StringBuilder deployCommand = new StringBuilder("");
        deployCommand.append(CLI_COMMAND_CREATESTACK + CLI_PARAM_STACKNAME)
            .append(cfnModule.getStackName()).append(" ")
            .append(CLI_PARAM_TEMPLATEFILE).append("../").append(TEMPLATE_YAML);

        // Add IAM capability if needed
        List<FileUpload> fileUploadList = cfnModule.getFileUploadList();
        if (!fileUploadList.isEmpty()) {
            logger.debug("Adding IAM capability to create stack command.");
            deployCommand.append(" " + CLI_PARAM_CAPABILITIES + " " + CAPABILITY_IAM);
        }

        // Add parameters if needed
        Map<String, Parameter> parameters = cfnModule.getParameters();
        if (!parameters.isEmpty()) {
            logger.debug("Adding parameters to create stack command.");
            deployCommand.append(" " + CLI_PARAM_PARAMOVERRIDES);
            for (Map.Entry<String, Parameter> entry : parameters.entrySet()) {
                String id = entry.getKey();
                deployCommand.append(" ").append(id).append("=$").append(id).append("Var");
            }
        }
        createStackScript.append(deployCommand.toString());
    }

    /**
     Creates a script to delete the S3Bucket and the deployed Stack.
     Note: May or may not be included in the final version. Currently used for quicker manual debugging.
     */
    private void writeCleanUpScript() throws IOException {
        logger.debug("Creating cleanup script.");
        BashScript cleanupScript = new BashScript(cfnModule.getFileAccess(), FILENAME_CLEANUP);
        // Delete Bucket
        cleanupScript.append(CLI_COMMAND_DELETEBUCKET + cfnModule.getBucketName() + " " + CLI_PARAM_FORCE);
        // Echo delete Stack
        cleanupScript.append(COMMAND_ECHO + STRING_DELETESTACK + cfnModule.getStackName());
        // Delete stack
        cleanupScript.append(CLI_COMMAND_DELETESTACK + CLI_PARAM_STACKNAME + cfnModule.getStackName());
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
        //Iterate over all files in the script list
        List<String> utilScripts = IOUtils.readLines(
            getClass().getResourceAsStream(FILEPATH_SCRIPTS_UTIL + "scripts-list"),
            Charsets.UTF_8
        );

        logger.debug("Copying util scripts to the target artifact.");
        copyUtilFile(utilScripts, FILEPATH_SCRIPTS_UTIL, UTIL_DIR_PATH);
    }

    /**
     Copies all needed cloudformation utility files to the target artifact.
     Note: Theses are the files that actually need to be uploaded and accessed by EC2 instances unlike the util scripts.
     */
    public void copyUtilDependencies() throws IOException {
        logger.debug("Copying util files to the target artifact.");

        List<String> utilFileUploadList = getFilePaths(
            getFileUploadByType(cfnModule.getFileUploadList(), UTIL));
        copyUtilFile(utilFileUploadList, FILEPATH_FILES_UTIL, FILEPATH_TARGET);
    }

    /**
     Adds file upload commands for all given files to the given script.

     @param files  to be uploaded
     @param script to add the file upload commands to
     */
    private void addFileUploadsToScript(List<String> files, BashScript script) {
        files.forEach((filePath) -> {
            String localFilePath = RELATIVE_DIRECTORY_PREFIX + filePath;
            try {
                script.append(uploadFile(filePath, localFilePath));
            } catch (IOException e) {
                throw new TransformationFailureException("Failed to add file uploads to the script.", e);
            }
        });
    }

    private void copyUtilFile(List<String> files, String resourcePath, String outputPath) throws IOException {
        PluginFileAccess fileAccess = cfnModule.getFileAccess();
        for (String file : files) {
            if (!file.isEmpty()) {
                //Copy the file into the desired directory
                logger.debug("Adding '{}' to the target artifact.", file);
                InputStreamReader input = new InputStreamReader(
                    getClass().getResourceAsStream(resourcePath + file)
                );
                BufferedWriter output = fileAccess.access(outputPath + file);
                IOUtils.copy(input, output);
                input.close();
                output.close();
            }
        }
    }
}
