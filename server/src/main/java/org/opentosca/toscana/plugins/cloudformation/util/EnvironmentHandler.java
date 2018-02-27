package org.opentosca.toscana.plugins.cloudformation.util;

import java.io.IOException;
import java.util.Map;

import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.plugins.cloudformation.CloudFormationModule;

import com.scaleset.cfbuilder.ec2.metadata.CFNCommand;
import com.scaleset.cfbuilder.ec2.metadata.CFNFile;
import org.slf4j.Logger;

import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.ABSOLUTE_FILE_PATH;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.CONFIG_CONFIGURE;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.CONFIG_SETS;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.MODE_500;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.OWNER_GROUP_ROOT;
import static org.opentosca.toscana.plugins.cloudformation.util.FileToBeUploaded.UploadFileType.OTHER;
import static org.opentosca.toscana.plugins.cloudformation.util.StackUtils.getFileURL;

public class EnvironmentHandler {
    private static final String ECHO = "echo ";
    private static final String REDIRECT_OUTPUT = ">> ";
    private static final String ETC = "/etc/";
    private static final String ETC_ENVIRONMENT = ETC + "environment";
    private static final String ETC_APACHE2_ENVVARS = ETC + "apache2/envvars";
    private static final String SET_ENV = "set-env-";

    public static final String APACHE_ENV_IMPORT = ECHO
        + "'." + ETC_ENVIRONMENT + "' "
        + REDIRECT_OUTPUT + ETC_APACHE2_ENVVARS;

    private CloudFormationModule cfnModule;
    private PluginFileAccess access;
    private final Logger logger;
    private final Map<String, Map<String, String>> environmentMap;

    public EnvironmentHandler(CloudFormationModule cfnModule, Logger logger) {
        this.cfnModule = cfnModule;
        this.access = cfnModule.getFileAccess();
        this.environmentMap = cfnModule.getEnvironmentMap();
        this.logger = logger;
    }

    /**
     Writes the necessary scripts to set the environment variables, adds them to the instances and cop
     */
    public void handleEnvironment() throws IOException {
        logger.debug("Handling environment variables.");
        writeSetEnvScripts();
        addSetEnvScriptsToInstances();
        addSetEnvScriptsToFileUploads();
    }

    /**
     Writes the setEnv scripts for each Instance.
     */
    private void writeSetEnvScripts() throws IOException {
        logger.debug("Writing setEnv scripts.");
        for (Map.Entry<String, Map<String, String>> instanceEnvironment : environmentMap.entrySet()) {
            CloudFormationScript setEnvScript = new CloudFormationScript(access, SET_ENV + instanceEnvironment.getKey());
            for (Map.Entry<String, String> environmentVariable : instanceEnvironment.getValue().entrySet()) {
                // Build the command to write environment variable to /etc/environment
                setEnvScript.append(ECHO
                    + environmentVariable.getKey() + "=$" + environmentVariable.getKey() + " "
                    + REDIRECT_OUTPUT + ETC_ENVIRONMENT);
            }
        }
    }

    /**
     Adds the setEnv scripts to their respective instances and adds commands to execute them.
     */
    private void addSetEnvScriptsToInstances() {
        logger.debug("Adding setEnv scripts to Instances.");
        for (Map.Entry<String, Map<String, String>> instanceEnvironment : environmentMap.entrySet()) {
            String nodeName = instanceEnvironment.getKey();
            String filePath = SET_ENV + instanceEnvironment.getKey() + ".sh";
            String cfnSource = getFileURL(cfnModule.getBucketName(), filePath);

            CFNFile cfnFile = new CFNFile(ABSOLUTE_FILE_PATH + filePath)
                .setSource(cfnSource)
                .setMode(MODE_500) //TODO Check what mode is needed (read? + execute?)
                .setOwner(OWNER_GROUP_ROOT) //TODO Check what Owner is needed
                .setGroup(OWNER_GROUP_ROOT);

            CFNCommand cfnCommand = new CFNCommand(filePath,
                ABSOLUTE_FILE_PATH + filePath) //file is the full path, so need for "./"
                .setCwd(ABSOLUTE_FILE_PATH);
            // Adds values of the environment variables to the environment of the setEnv scripts
            for (Map.Entry<String, String> environmentVariable : instanceEnvironment.getValue().entrySet()) {
                String value = environmentVariable.getValue();
                if (cfnModule.checkFn(value)) {
                    cfnCommand.addEnv(environmentVariable.getKey(), cfnModule.getFn(value));
                } else {
                    cfnCommand.addEnv(environmentVariable.getKey(), value);
                }
            }

            cfnModule.getCFNInit(nodeName)
                .getOrAddConfig(CONFIG_SETS, CONFIG_CONFIGURE)
                .putFile(cfnFile)
                .putCommand(cfnCommand);
        }
    }

    /**
     Marks all the setEnv Scripts as files to be uploaded.
     */
    private void addSetEnvScriptsToFileUploads() {
        logger.debug("Marking setEnv scripts to files to be uploaded.");
        for (Map.Entry<String, Map<String, String>> instanceEnvironment : environmentMap.entrySet()) {
            cfnModule.addFileToBeUploaded(new FileToBeUploaded(SET_ENV + instanceEnvironment.getKey() + ".sh", OTHER));
        }
    }
}
