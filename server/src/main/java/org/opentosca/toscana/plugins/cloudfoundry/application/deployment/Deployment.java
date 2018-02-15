package org.opentosca.toscana.plugins.cloudfoundry.application.deployment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.plugins.cloudfoundry.application.Application;
import org.opentosca.toscana.plugins.cloudfoundry.application.ServiceTypes;
import org.opentosca.toscana.plugins.scripts.BashScript;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import static org.opentosca.toscana.core.plugin.lifecycle.AbstractLifecycle.SCRIPTS_DIR_PATH;

/**
 this class provides methods to create the deployment script
 */
public class Deployment {

    private Logger logger;

    private BashScript deploymentScript;
    private Application application;
    private PluginFileAccess fileAccess;
    private Class deploymentClass;

    private final String PYTHON_SCRIPTS_TARGET = SCRIPTS_DIR_PATH;
    private final String PYTHON_SCRIPTS_SOURCE = "/cloudFoundry/deployment_scripts/";

    private final String PYTHON_CONFIGURE_SQL_FILENAME = "configureMysql.py";
    private final String PYTHON_EXECUTE_FILENAME = "executeCommand.py";
    private final String PYTHON_READ_CREDENTIALS_FILENAME = "readCredentials.py";
    private final String PYTHON_REPLACE_STRINGS_FILENAME = "replace.py";

    private TransformationContext context;

    public Deployment(BashScript deploymentScript, Application application, PluginFileAccess fileAccess, TransformationContext context) throws IOException {
        this.deploymentScript = deploymentScript;
        this.application = application;
        this.fileAccess = fileAccess;
        deploymentClass = Deployment.class;
        this.logger = context.getLogger(getClass());
        this.context = context;
    }

    /**
     look for suitable services which match to the requirements of the user
     */
    public void treatServices() throws IOException {
        ServiceHandler serviceHandler = new ServiceHandler(application, deploymentScript, context);

        String scriptPath = deploymentScript.getScriptPath();
        File scriptFile = new File(scriptPath);
        String contentScript = FileUtils.readFileToString(scriptFile);

        serviceHandler.addServiceCommands(!contentScript.contains("cf create-service"));
    }

    /**
     will add a python script which executes the given sql file to the database.
     Only works with a mysql service and a .sql file

     @param relativePathToSQLConfigureFile must be the relative path started from the output/scripts folder to the .sql
     file
     */
    public void configureSql(String relativePathToSQLConfigureFile) throws IOException {
        copyFile(PYTHON_CONFIGURE_SQL_FILENAME);

        String command = String.format("python %s %s", PYTHON_CONFIGURE_SQL_FILENAME, relativePathToSQLConfigureFile);
        logger.debug("Add \"{}\" to deploy script. This command will configure the sql database. File should be a .sql File", command);
        deploymentScript.append(command);
    }

    /**
     will add a python script which executes a file on the warden container

     @param appName               name of the created app
     @param pathToFileOnContainer must be the path on the warden container. Probably something like that:
     "/home/vcap/app..."
     */
    public void executeFile(String appName, String pathToFileOnContainer) throws IOException {
        copyFile(PYTHON_EXECUTE_FILENAME);
        deploymentScript.append(String.format("python %s %s %s", PYTHON_EXECUTE_FILENAME, appName, pathToFileOnContainer));
    }

    /**
     will add a python script which read out the credentials of a service and write it to environment variables of the
     container

     @param appName     name of the created app
     @param serviceName must be the service name of the provider not the service instance name
     @param serviceType e.g. "mysql" for a mysql service
     */
    public void readCredentials(String appName, String serviceName, ServiceTypes serviceType, String serviceInstanceName) throws IOException {
        copyFile(PYTHON_READ_CREDENTIALS_FILENAME);
        deploymentScript.append(String.format("python %s %s %s %s %s", PYTHON_READ_CREDENTIALS_FILENAME, appName, serviceName, serviceType.getName(), serviceInstanceName));
    }

    /**
     will add a python script which replaces all occurrence of a string inside a file.

     @param pathToLocalFile must be the relative path started from the output/scripts folder to the file
     @param findStr         String which will be replaced
     @param replaceStr      String which replaces the findStr
     */
    public void replaceStrings(String pathToLocalFile, String findStr, String replaceStr) throws IOException {
        copyFile(PYTHON_REPLACE_STRINGS_FILENAME);
        deploymentScript.append(String.format("python %s %s %s %s", PYTHON_REPLACE_STRINGS_FILENAME, pathToLocalFile, findStr, replaceStr));
    }

    /**
     copies the python script files to the output/scripts folder

     @param fileName python script name
     */
    private void copyFile(String fileName) throws IOException {
        logger.debug("Copy python script {} to output folder", fileName);
        if (!isAlreadyCopied(fileName)) {
            InputStream inputStream = deploymentClass.getResourceAsStream(PYTHON_SCRIPTS_SOURCE + fileName);
            String contentFile = IOUtils.toString(inputStream);
            inputStream.close();
            fileAccess.access(PYTHON_SCRIPTS_TARGET + fileName).appendln(contentFile).close();
        }
        checkPython();
    }

    /**
     checks if a file is already copied.
     */
    private boolean isAlreadyCopied(String fileName) {
        try {
            fileAccess.getAbsolutePath(PYTHON_SCRIPTS_TARGET + fileName);
        } catch (FileNotFoundException e) {
            logger.debug("File {} is not in the output folder, will be copied", fileName);
            return false;
        }
        logger.debug("File {} is already in the output folder", fileName);
        return true;
    }

    /**
     adds a shell command to check if python is installed
     */
    private void checkPython() throws IOException {
        String scriptPath = deploymentScript.getScriptPath();
        File scriptFile = new File(scriptPath);
        String contentScript = FileUtils.readFileToString(scriptFile);

        if (!contentScript.contains("check python")) {
            deploymentScript.append("check python");
        }
    }
}
