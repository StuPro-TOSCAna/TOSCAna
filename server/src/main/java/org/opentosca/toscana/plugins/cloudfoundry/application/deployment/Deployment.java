package org.opentosca.toscana.plugins.cloudfoundry.application.deployment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.plugins.cloudfoundry.application.Application;
import org.opentosca.toscana.plugins.cloudfoundry.application.ServiceTypes;
import org.opentosca.toscana.plugins.scripts.BashScript;

import org.apache.commons.io.IOUtils;

import static org.opentosca.toscana.plugins.lifecycle.AbstractLifecycle.OUTPUT_DIR;

public class Deployment {

    private BashScript deploymentScript;
    private Application application;
    private PluginFileAccess fileAccess;
    private Class clazz;

    private final String PYTHON_SCRIPTS_TARGET = "scripts/";

    private final String PYTHON_CONFIGURE_SQL_FILENAME = "configureMysql.py";
    private final String PYTHON_CONFIGURE_SQL_SOURCE = "/cloudFoundry/deployment_scripts/";

    private final String PYTHON_EXECUTE_FILENAME = "executeCommand.py";
    private final String PYTHON_EXECUTE_SOURCE = "/cloudFoundry/deployment_scripts/";

    private final String PYTHON_READ_CREDENTIALS_FILENAME = "readCredentials.py";
    private final String PYTHON_READ_CREDENTIALS_SOURCE = "/cloudFoundry/deployment_scripts/";

    private final String PYTHON_REPLACE_STRINGS_FILENAME = "replace.py";
    private final String PYTHON_REPLACE_STRINGS_SOURCE = "/cloudFoundry/deployment_scripts/";

    public Deployment(BashScript deploymentScript, Application application, PluginFileAccess fileAccess) throws IOException {
        this.deploymentScript = deploymentScript;
        this.application = application;
        this.fileAccess = fileAccess;
        clazz = Deployment.class;
        deploymentScript.append("check python");
    }

    /**
     look for suitable services which match to the requirements of the user

     @param showAllServiceOfferings if yes in the deployment script will be all offered services added
     */
    public void treatServices(Boolean showAllServiceOfferings) {
        ServiceHandler serviceHandler = new ServiceHandler(application, deploymentScript);
        serviceHandler.addServiceCommands(showAllServiceOfferings);
    }

    /**
     will add a python script which executes the given sql file to the database.
     Only works with a mysql service and a .sql file

     @param relativePathToSQLConfigureFile must be the relative path started from the output/scripts folder to the .sql
     file
     */
    public void addConfigureSql(String relativePathToSQLConfigureFile) throws IOException {
        copyFile(PYTHON_CONFIGURE_SQL_FILENAME, PYTHON_CONFIGURE_SQL_SOURCE);
        deploymentScript.append(String.format("python %s %s", PYTHON_CONFIGURE_SQL_FILENAME, relativePathToSQLConfigureFile));
    }

    /**
     will add a python script which executes a file on the warden container

     @param appName               name of the created app
     @param pathToFileOnContainer must be the path on the warden container. Probably something like that:
     "/home/vcap/app..."
     */
    public void addExecuteFile(String appName, String pathToFileOnContainer) throws IOException {
        copyFile(PYTHON_EXECUTE_FILENAME, PYTHON_EXECUTE_SOURCE);
        deploymentScript.append(String.format("python %s %s %s", PYTHON_EXECUTE_FILENAME, appName, pathToFileOnContainer));
    }

    /**
     will add a python script which read out the credentials of a service and write it to environment variables of the
     container

     @param appName     name of the created app
     @param serviceName must be the service name of the provider not the service instance name
     @param serviceType e.g. "mysql" for a mysql service
     */
    public void addReadCredentials(String appName, String serviceName, ServiceTypes serviceType) throws IOException {
        copyFile(PYTHON_READ_CREDENTIALS_FILENAME, PYTHON_READ_CREDENTIALS_SOURCE);
        deploymentScript.append(String.format("python %s %s %s", PYTHON_READ_CREDENTIALS_FILENAME, serviceName, serviceType.getName()));
    }

    /**
     will add a python script which replaces all occurrence of a string inside a file.

     @param pathToLocalFile must be the relative path started from the output/scripts folder to the file
     @param findStr         String which will be replaced
     @param replaceStr      String which replaces the findStr
     */
    public void addReplaceStrings(String pathToLocalFile, String findStr, String replaceStr) throws IOException {
        copyFile(PYTHON_REPLACE_STRINGS_FILENAME, PYTHON_REPLACE_STRINGS_SOURCE);
        deploymentScript.append(String.format("python %s %s %s %s", PYTHON_REPLACE_STRINGS_FILENAME, pathToLocalFile, findStr, replaceStr));
    }

    private void copyFile(String fileName, String source) throws IOException {
        if (!isAlreadyCopied(fileName, source)) {
            InputStream inputStream = clazz.getResourceAsStream(source + fileName);
            String contentFile = IOUtils.toString(inputStream);
            inputStream.close();
            fileAccess.access(OUTPUT_DIR + PYTHON_SCRIPTS_TARGET + fileName).appendln(contentFile).close();
        }
    }

    private boolean isAlreadyCopied(String fileName, String source) {
        try {
            fileAccess.getAbsolutePath(OUTPUT_DIR + PYTHON_SCRIPTS_TARGET + fileName);
        } catch (FileNotFoundException e) {
            return false;
        }
        return true;
    }
}
