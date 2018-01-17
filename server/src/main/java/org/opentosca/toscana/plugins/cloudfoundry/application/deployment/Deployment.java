package org.opentosca.toscana.plugins.cloudfoundry.application.deployment;

import java.io.IOException;
import java.io.InputStream;

import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.plugins.cloudfoundry.application.Application;
import org.opentosca.toscana.plugins.scripts.BashScript;

import org.apache.commons.io.IOUtils;

import static org.opentosca.toscana.plugins.lifecycle.AbstractLifecycle.OUTPUT_DIR;

public class Deployment {

    private BashScript deploymentScript;
    private Application application;
    private PluginFileAccess fileAccess;
    private Class clazz;

    private final String PYTHON_CONFIGURE_SQL = "/cloudFoundry/deployment_scripts/configureMysql.py";
    private final String PYTHON_CONFIGURE_SQL_TARGET = "scripts/configureMysql.py";

    private final String PYTHON_EXECUTE_FILE = "/cloudFoundry/deployment_scripts/executeCommand.py";
    private final String PYTHON_EXECUTE_FILE_TARGET = "scripts/executeCommand.py";
    private final String PYTHON_READ_CREDENTIALS = "/cloudFoundry/deployment_scripts/readCredentials.py";
    private final String PYTHON_REPLACE_STRINGS = "/cloudFoundry/deployment_scripts/replace.py";

    public Deployment(BashScript deploymentScript, Application application, PluginFileAccess fileAccess) throws IOException {
        this.deploymentScript = deploymentScript;
        this.application = application;
        this.fileAccess = fileAccess;
        clazz = Deployment.class;
        deploymentScript.append("check python");
    }

    public void treatServices(Boolean showAllServiceOfferings) {
        ServiceHandler serviceHandler = new ServiceHandler(application, deploymentScript);
        serviceHandler.addServiceCommands(showAllServiceOfferings);
    }

    public void addConfigureSql(String relativePathToSQLConfigureFile) throws IOException {
        InputStream configSql = clazz.getResourceAsStream(PYTHON_CONFIGURE_SQL);
        String configureMysqlPython = IOUtils.toString(configSql);
        configSql.close();
        fileAccess.access(OUTPUT_DIR + PYTHON_CONFIGURE_SQL_TARGET).appendln(configureMysqlPython).close();
        
        deploymentScript.append(String.format("python configureMysql.py %s", relativePathToSQLConfigureFile));
        
    }

    public void addExecuteFile(String appName, String pathToFileOnContainer) throws IOException {
        InputStream execute = clazz.getResourceAsStream(PYTHON_EXECUTE_FILE);
        String executeFilePython = IOUtils.toString(execute);
        execute.close();
        fileAccess.access(OUTPUT_DIR + PYTHON_EXECUTE_FILE_TARGET).appendln(executeFilePython).close();

        deploymentScript.append(String.format("python executeCommand.py %s %s", appName, pathToFileOnContainer));
    }

    public void addReadCredentials() {

    }

    public void addReplaceStrings() {

    }

    private boolean isAlreadyCopied() {
        
        return false;
    }
}
