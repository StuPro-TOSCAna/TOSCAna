package org.opentosca.toscana.plugins.cloudformation.util;

import java.util.Map;

import org.opentosca.toscana.plugins.cloudformation.CloudFormationModule;

import org.slf4j.Logger;

public class EnvironmentHandler {
    private static final String ECHO = "echo";
    private static final String REDIRECT_OUTPUT = ">>";
    private static final String ETC_ENVIRONMENT = "/etc/environment";
    
    private CloudFormationModule cfnModule;
    private final Logger logger;

    public EnvironmentHandler(CloudFormationModule cfnModule, Logger logger) {
        this.cfnModule = cfnModule;
        this.logger = logger;
    }

    /**
     Writes the necessary scripts to set the environment variables, adds them to the instances and cop
     */
    public void handleEnvironment() {
        logger.debug("Handling environment variables.");
        Map<String, Map<String, String>> environmentMap = cfnModule.getEnvironmentMap();
        writeSetEnvScripts(environmentMap);
        addSetEnvScriptsToInstances(environmentMap);
        copySetEnvScripts(environmentMap);
        addSetEnvScriptsToFileUploads(environmentMap);
    }

    /**
     Writes the setEnv scripts for each Instance.
     
     @param environmentMap containing the instance names and environment variables
     */
    private void writeSetEnvScripts(Map<String, Map<String, String>> environmentMap) {
        logger.debug("Writing setEnv scripts.");
        for (Map.Entry<String, Map<String, String>> instanceEnvironment : environmentMap.entrySet()) {
            // TODO create setEnv-instance script
            for (Map.Entry<String, String> environmentVariable : instanceEnvironment.getValue().entrySet()) {
                // Build the command to write environment variable to /etc/environment
                String command = ECHO + 
                    " '" + environmentVariable.getKey() + "=\"" + environmentVariable.getValue() + "\" "
                    + REDIRECT_OUTPUT + " " + ETC_ENVIRONMENT;
                // TODO append command to setEnv-instance  
            }
        }
    }

    /**
     Adds the setEnv scripts to their respective instances.
     
     @param environmentMap containing the instance names and environment variables
     */
    private void addSetEnvScriptsToInstances(Map<String, Map<String, String>> environmentMap) {
        logger.debug("Adding setEnv scripts to Instances.");
        for (Map.Entry<String, Map<String, String>> instanceEnvironment : environmentMap.entrySet()) {
            // TODO add setEnv -instance script to instance
        }
    }

    /**
     Copies all the setEnv scripts to the target artifact.
     
     @param environmentMap containing the instance names and environment variables
     */
    private void copySetEnvScripts(Map<String, Map<String, String>> environmentMap) {
        logger.debug("Copying setEnv scripts.");
        for (Map.Entry<String, Map<String, String>> instanceEnvironment : environmentMap.entrySet()) {
            // TODO copy setEnv -instance script to target artifact
        }
    }

    /**
     Marks all the setEnv Scripts as files to be uploaded.
     
     @param environmentMap containing the instance names and environment variables
     */
    private void addSetEnvScriptsToFileUploads(Map<String, Map<String, String>> environmentMap) {
        logger.debug("Marking setEnv scripts to files to be uploaded.");
        for (Map.Entry<String, Map<String, String>> instanceEnvironment : environmentMap.entrySet()) {
            //TODO mark setEnv-instance script as file to be uploaded
        }
    }
}
