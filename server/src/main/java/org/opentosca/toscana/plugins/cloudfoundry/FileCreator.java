package org.opentosca.toscana.plugins.cloudfoundry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.plugins.cloudfoundry.application.Application;
import org.opentosca.toscana.plugins.cloudfoundry.application.ServiceTypes;
import org.opentosca.toscana.plugins.cloudfoundry.application.buildpacks.BuildpackDetector;
import org.opentosca.toscana.plugins.cloudfoundry.application.deployment.Deployment;
import org.opentosca.toscana.plugins.scripts.BashScript;
import org.opentosca.toscana.plugins.scripts.EnvironmentCheck;

import org.json.JSONException;

import static org.opentosca.toscana.plugins.cloudfoundry.application.ManifestAttributes.DOMAIN;
import static org.opentosca.toscana.plugins.cloudfoundry.application.ManifestAttributes.ENVIRONMENT;
import static org.opentosca.toscana.plugins.cloudfoundry.application.ManifestAttributes.PATH;
import static org.opentosca.toscana.plugins.cloudfoundry.application.ManifestAttributes.RANDOM_ROUTE;
import static org.opentosca.toscana.plugins.cloudfoundry.application.ManifestAttributes.SERVICE;
import static org.opentosca.toscana.plugins.lifecycle.AbstractLifecycle.OUTPUT_DIR;

/**
 Creates all files which are necessary to deploy the application
 Files: manifest.yml, builpack additions, deployScript
 */
public class FileCreator {

    public static final String MANIFEST_NAME = "manifest.yml";
    public static final String MANIFEST_PATH = OUTPUT_DIR + MANIFEST_NAME;
    public static final String MANIFESTHEAD = "---\napplications:\n";
    public static final String NAMEBLOCK = "name";
    public static final String CLI_CREATE_SERVICE_DEFAULT = "cf create-service {plan} {service} ";
    public static final String CLI_CREATE_SERVICE = "cf create-service ";
    public static final String CLI_PUSH = "cf push ";
    public static final String CLI_PATH_TO_MANIFEST = " -f ../";
    public static final String FILEPRAEFIX_DEPLOY = "deploy_";
    public static final String FILESUFFIX_DEPLOY = ".sh";
    public static final String APPLICATION_FOLDER = "app";

    private final PluginFileAccess fileAccess;
    private final Application app;

    public FileCreator(PluginFileAccess fileAccess, Application app) {
        this.fileAccess = fileAccess;
        this.app = app;
    }

    public void createFiles() throws IOException, JSONException {
        createManifest();
        createBuildpackAdditionsFile();
        createDeployScript();
        insertFiles(APPLICATION_FOLDER + app.getApplicationNumber());
    }

    private void createManifest() throws IOException {
        createManifestHead();
        addPathToApplication();
        createAttributes();
        createEnvironmentVariables();
        createService();
    }

    public void updateManifest() throws IOException {
        fileAccess.delete(MANIFEST_PATH);
        createManifest();
    }

    private void createManifestHead() throws IOException {
        String manifestHead = String.format("%s- %s: %s", MANIFESTHEAD, NAMEBLOCK, app.getName());
        fileAccess.access(MANIFEST_PATH).appendln(manifestHead).close();
    }

    /**
     adds the relative path of the application folder to the manifest
     */
    private void addPathToApplication() throws IOException {
        String pathAddition = String.format("  %s: ../%s", PATH.getName(), APPLICATION_FOLDER + app.getApplicationNumber());
        fileAccess.access(MANIFEST_PATH).appendln(pathAddition).close();
    }

    private void createEnvironmentVariables() throws IOException {
        Map<String, String> envVariables = app.getEnvironmentVariables();
        if (!envVariables.isEmpty()) {
            ArrayList<String> environmentVariables = new ArrayList<>();
            environmentVariables.add(String.format("  %s:", ENVIRONMENT.getName()));
            for (Map.Entry<String, String> entry : envVariables.entrySet()) {
                environmentVariables.add(String.format("    %s: %s", entry.getKey(), entry.getValue()));
            }
            for (String env : environmentVariables) {
                fileAccess.access(MANIFEST_PATH).appendln(env).close();
            }
        }
    }

    /**
     creates a service which depends on the template
     add it to the manifest
     */
    private void createService() throws IOException {
        Map<String, ServiceTypes> appServices = app.getServices();
        if (!appServices.isEmpty()) {
            ArrayList<String> services = new ArrayList<>();
            services.add(String.format("  %s:", SERVICE.getName()));
            for (Map.Entry<String, ServiceTypes> service : appServices.entrySet()) {
                services.add(String.format("    - %s", service.getKey()));
            }
            for (String service : services) {
                fileAccess.access(MANIFEST_PATH).appendln(service).close();
            }
        }
    }

    /**
     creates a deploy shell script
     */
    private void createDeployScript() throws IOException {
        BashScript deployScript = new BashScript(fileAccess, FILEPRAEFIX_DEPLOY + app.getName());
        deployScript.append(EnvironmentCheck.checkEnvironment("cf"));

        Deployment deployment = new Deployment(deployScript, app, fileAccess);
        deployment.treatServices(true);
        //deployment.addConfigureSql();

        deployScript.append(CLI_PUSH + app.getName() + CLI_PATH_TO_MANIFEST + MANIFEST_NAME);
    }

    /**
     detect if additional buildpacks are needed and add them
     */
    private void createBuildpackAdditionsFile() throws IOException, JSONException {
        BuildpackDetector buildpackDetection = new BuildpackDetector(app, fileAccess);
        buildpackDetection.detectBuildpackAdditions();
    }

    private void createAttributes() throws IOException {

        if (!app.getAttributes().isEmpty()) {
            ArrayList<String> attributes = new ArrayList<>();
            boolean containsDomain = false;
            for (Map.Entry<String, String> attribute : app.getAttributes().entrySet()) {
                attributes.add(String.format("  %s: %s", attribute.getKey(), attribute.getValue()));
                if (attribute.getKey().equals(DOMAIN.getName())) {
                    containsDomain = true;
                }
            }
            if (!containsDomain) {
                attributes.add(String.format("  %s: %s", RANDOM_ROUTE.getName(), "true"));
            }
            if (!app.getAttributes().containsKey(DOMAIN.getName())) {
                attributes.add(String.format("  %s: %s", RANDOM_ROUTE.getName(), "true"));
            }
            for (String attribute : attributes) {
                fileAccess.access(MANIFEST_PATH).appendln(attribute).close();
            }
        } else {
            String randomRouteAttribute = String.format("  %s: %s", RANDOM_ROUTE.getName(), "true");
            fileAccess.access(MANIFEST_PATH).appendln(randomRouteAttribute).close();
        }
    }

    private void insertFiles(String applicationFolder) throws IOException {
        for (String filePath : app.getFilePaths()) {
            String path = applicationFolder + "/" + filePath;
            fileAccess.copy(filePath, path);
        }
    }
}
