package org.opentosca.toscana.plugins.cloudfoundry.application.deployment;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

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
    private final String PYTHON_EXECUTE_FILE = "/cloudFoundry/deployment_scripts/executeCommand.py";
    private final String PYTHON_READ_CREDENTIALS = "/cloudFoundry/deployment_scripts/readCredentials.py";
    private final String PYTHON_REPLACE_STRINGS = "/cloudFoundry/deployment_scripts/replace.py";

    public Deployment(BashScript deploymentScript, Application application, PluginFileAccess fileAccess) {
        this.deploymentScript = deploymentScript;
        this.application = application;
        this.fileAccess = fileAccess;
        clazz = Deployment.class;
    }

    public void treatServices(Boolean showAllServiceOfferings) {
        ServiceHandler serviceHandler = new ServiceHandler(application, deploymentScript);
        serviceHandler.addServiceCommands(showAllServiceOfferings);
    }

    public void addConfigureSql() throws IOException{
        InputStream configSql = clazz.getResourceAsStream(PYTHON_CONFIGURE_SQL);
        String configureMysqlPython = IOUtils.toString(configSql);
        configSql.close();
        fileAccess.access(OUTPUT_DIR + "/scripts/configureMysql.py").appendln(configureMysqlPython);
    }

    public void addExecuteFile() {

    }

    public void addReadCredentials() {

    }

    public void addReplaceStrings() {

    }

    private boolean isAlreadyCopied() {

        return false;
    }
}
