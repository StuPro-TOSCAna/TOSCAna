package org.opentosca.toscana.plugins.cloudfoundry;

import java.io.IOException;
import java.util.Map;

import org.opentosca.toscana.core.plugin.PluginFileAccess;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public void createFiles() throws IOException, JSONException {
        createManifest();
        createBuildpackAdditionsFile();
        createDeployScript();
    }

    private void createManifest() throws IOException {
        String manifestContent;
        manifestContent = createManifestHead();
        manifestContent += createAttributes();
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
        String deploy = null;
        for (String service : app.getServices()) {
            deploy += "cf create-service {plan} {service} " + service + "\n";
        }
        deploy += "cf push " + app.getAppName();

        fileAccess.access("/deploy_" + app.getAppName() + ".sh")
            .append(deploy).close();
    }

    //only for PHP
    private void createBuildpackAdditionsFile() throws IOException, JSONException {
        JSONObject buildPackAdditionsJson = new JSONObject();
        JSONArray buildPacks = new JSONArray();
        for (String buildPack : app.getBuildpackAdditions()) {
            buildPacks.put(buildPack);
        }
        buildPackAdditionsJson.put("PHP-EXTENSIONS", buildPacks);
        fileAccess.access("/.bp-config/options.json").append(buildPackAdditionsJson.toString()).close();
    }

    private String createAttributes() {
        String attributes = "\n";

        for (Map.Entry<String, String> attribute : app.getAttributes().entrySet()) {
            attributes += "  " + attribute.getKey() + ": " + attribute.getValue() + "\n";
        }
        return attributes;
    }

}
