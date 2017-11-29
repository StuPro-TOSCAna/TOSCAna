package org.opentosca.toscana.plugins.cloudfoundry;

import java.io.IOException;
import java.util.Map;

import org.opentosca.toscana.core.plugin.PluginFileAccess;

/**
 Creates all files which are necessary to deploy the application
 Files: manifest.yml, builpack additions, deployScript
 */
public class CloudFoundryFileCreator {

    private final PluginFileAccess fileAccess;
    private final CloudFoundryApplication app;

    public CloudFoundryFileCreator(PluginFileAccess fileAccess, CloudFoundryApplication app) {
        this.fileAccess = fileAccess;
        this.app = app;
    }

    public void createFiles() throws IOException {
        createManifest();
        createBuildpackAdditionsFile();
        createDeployScript();
    }

    private void createManifest() throws IOException {
        String manifestContent;
        manifestContent = createManifestHead();
        manifestContent += createEnvironmentVariables();
        manifestContent += createService();
        fileAccess.access("/manifest.yml").append(manifestContent).close();
    }

    private String createManifestHead() {
        return "applications: \n- name: " + app.getAppName();
    }

    private String createEnvironmentVariables() {
        String envBlock;
        envBlock = "\n  env:";
        for (Map.Entry<String, String> entry : app.getEnvironmentVariables().entrySet()) {
            envBlock = envBlock + "\n    " + entry.getKey() + ": " + entry.getValue();
        }
        return envBlock;
    }

    private String createService() {
        String serviceBlock;
        serviceBlock = "\n  service:";
        for (String service : app.getServices()) {
            serviceBlock += "\n    - " + service;
        }
        return serviceBlock;
    }

    private void createDeployScript() throws IOException {
        fileAccess.access("/deploy_" + app.getAppName() + ".sh")
            .append("cf -push " + app.getAppName()).close();
    }

    private void createBuildpackAdditionsFile() throws IOException {
        String buildPackAdditions = "{\"PHP-EXTENSIONS\":[";
        for (String bp : app.getBuildpackAdditions()) {
            buildPackAdditions += "\"" + bp + "\",";
        }
        buildPackAdditions += "]}";
        fileAccess.access("/.bp-config/options.json").append(buildPackAdditions).close();
    }
}
