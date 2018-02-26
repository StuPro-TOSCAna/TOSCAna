package org.opentosca.toscana.plugins.cloudformation.util;

import java.io.IOException;
import java.util.Map;

import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.plugins.cloudformation.CloudFormationModule;

import org.slf4j.Logger;

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

    private PluginFileAccess access;
    private final Logger logger;
    private final Map<String, Map<String, String>> environmentMap;

    public EnvironmentHandler(CloudFormationModule cfnModule, Logger logger) {
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
        copySetEnvScripts();
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
                setEnvScript.append(ECHO +
                    "'" + environmentVariable.getKey() + "=\"" + environmentVariable.getValue() + "\" "
                    + REDIRECT_OUTPUT + ETC_ENVIRONMENT);
            }
        }
    }

    /**
     Adds the setEnv scripts to their respective instances.
     */
    private void addSetEnvScriptsToInstances() {
        logger.debug("Adding setEnv scripts to Instances.");
        for (Map.Entry<String, Map<String, String>> instanceEnvironment : environmentMap.entrySet()) {
            // TODO add setEnv -instance script to instance
        }
    }

    /**
     Copies all the setEnv scripts to the target artifact.
     */
    private void copySetEnvScripts() {
        logger.debug("Copying setEnv scripts.");
        for (Map.Entry<String, Map<String, String>> instanceEnvironment : environmentMap.entrySet()) {
            // TODO copy setEnv -instance script to target artifact
        }
    }

    /**
     Marks all the setEnv Scripts as files to be uploaded.
     */
    private void addSetEnvScriptsToFileUploads() {
        logger.debug("Marking setEnv scripts to files to be uploaded.");
        for (Map.Entry<String, Map<String, String>> instanceEnvironment : environmentMap.entrySet()) {
            //TODO mark setEnv-instance script as file to be uploaded
        }
    }
}
