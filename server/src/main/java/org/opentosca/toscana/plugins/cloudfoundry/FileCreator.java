package org.opentosca.toscana.plugins.cloudfoundry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.plugins.cloudfoundry.application.Application;
import org.opentosca.toscana.plugins.cloudfoundry.application.Service;
import org.opentosca.toscana.plugins.cloudfoundry.application.ServiceTypes;
import org.opentosca.toscana.plugins.cloudfoundry.application.buildpacks.BuildpackDetector;
import org.opentosca.toscana.plugins.cloudfoundry.application.deployment.Deployment;
import org.opentosca.toscana.plugins.scripts.BashScript;
import org.opentosca.toscana.plugins.scripts.EnvironmentCheck;

import org.apache.commons.collections.CollectionUtils;
import org.json.JSONException;

import static org.opentosca.toscana.core.plugin.lifecycle.AbstractLifecycle.OUTPUT_DIR;
import static org.opentosca.toscana.plugins.cloudfoundry.application.ManifestAttributes.DOMAIN;
import static org.opentosca.toscana.plugins.cloudfoundry.application.ManifestAttributes.ENVIRONMENT;
import static org.opentosca.toscana.plugins.cloudfoundry.application.ManifestAttributes.PATH;
import static org.opentosca.toscana.plugins.cloudfoundry.application.ManifestAttributes.RANDOM_ROUTE;
import static org.opentosca.toscana.plugins.cloudfoundry.application.ManifestAttributes.SERVICE;

/**
 Creates all files which are necessary to deploy the application
 Files: manifest.yml, builpack additions, deployScript
 */
public class FileCreator {

    public static final String MANIFEST_NAME = "manifest.yml";
    public static final String MANIFEST_PATH = OUTPUT_DIR + MANIFEST_NAME;
    public static final String MANIFESTHEAD = "---\napplications:";
    public static final String NAMEBLOCK = "name";
    public static final String CLI_CREATE_SERVICE_DEFAULT = "cf create-service {plan} {service} ";
    public static final String CLI_CREATE_SERVICE = "cf create-service ";
    public static final String CLI_PUSH = "cf push ";
    public static final String CLI_PATH_TO_MANIFEST = " -f ../";
    public static final String FILEPRAEFIX_DEPLOY = "deploy_";
    public static final String FILESUFFIX_DEPLOY = ".sh";
    public static final String APPLICATION_FOLDER = "app";
    public static String deploy_name = "application";

    private final PluginFileAccess fileAccess;
    private List<Application> applications;

    public FileCreator(PluginFileAccess fileAccess, List<Application> applications) {
        this.fileAccess = fileAccess;
        this.applications = applications;
    }

    public void createFiles() throws IOException, JSONException {
        createManifest();
        createDeployScript();

        for (Application application : applications) {
            createBuildpackAdditionsFile(application);
            insertFiles(application);
        }
    }

    private void createManifest() throws IOException {
        createManifestHead();
        for (Application application : applications) {
            addNameToManifest(application);
            addPathToApplication(application);
            createAttributes(application);
            createEnvironmentVariables(application);
            createService(application);
        }
    }

    private void createManifestHead() throws IOException {
        String manifestHead = String.format(MANIFESTHEAD);
        fileAccess.access(MANIFEST_PATH).appendln(manifestHead).close();
    }

    private void addNameToManifest(Application application) throws IOException {
        String nameBlock = String.format("- %s: %s", NAMEBLOCK, application.getName());
        fileAccess.access(MANIFEST_PATH).appendln(nameBlock).close();
    }

    /**
     adds the relative path of the application folder to the manifest
     */
    private void addPathToApplication(Application application) throws IOException {
        String pathAddition = String.format("  %s: ../%s", PATH.getName(), APPLICATION_FOLDER + application.getApplicationNumber());
        fileAccess.access(MANIFEST_PATH).appendln(pathAddition).close();
    }

    private void createEnvironmentVariables(Application application) throws IOException {
        Map<String, String> envVariables = application.getEnvironmentVariables();
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
    private void createService(Application application) throws IOException {
        Map<String, ServiceTypes> appServices = application.getServices();
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
        if (applications.size() > 1) {
            deploy_name += "s";
        }
        BashScript deployScript = new BashScript(fileAccess, FILEPRAEFIX_DEPLOY + deploy_name);
        deployScript.append(EnvironmentCheck.checkEnvironment("cf"));

        //handle services
        handleServices(deployScript);

        //replace
        replaceStrings(deployScript);

        //push applications
        for (Application application : applications) {
            deployScript.append(CLI_PUSH + application.getName() + CLI_PATH_TO_MANIFEST + MANIFEST_NAME);
        }

        //read credentials, replace, executeScript, configureMysql
        for (Application application : applications) {
            Deployment deployment = new Deployment(deployScript, application, fileAccess);

            //read credentials
            readCredentials(deployment, application);

            //execute
            executeFiles(deployment, application);

            //configureSql
            configureSql(deployment, application);
        }
    }

    /**
     the files in the application which are signed as sql and config scripts will be executed in the database
     */
    private void configureSql(Deployment deployment, Application application) throws IOException {
        if (!application.getConfigMysql().isEmpty()) {
            for (String file : application.getConfigMysql()) {
                deployment.configureSql(file);
            }
        }
    }

    /**
     adds to deploy script a command which will execute the files which are in the application signed to execute
     */
    private void executeFiles(Deployment deployment, Application application) throws IOException {
        if (!application.getExecuteCommands().isEmpty()) {
            Map<String, String> executeCommands = application.getExecuteCommands();

            for (Map.Entry<String, String> command : executeCommands.entrySet()) {
                deployment.executeFile(application.getName(), command.getValue());
            }
        }
    }

    /**
     adds for each service a command to the deploy script which reads the credentials from the service which will be
     created
     */
    private void readCredentials(Deployment deployment, Application application) throws IOException {
        if (!CollectionUtils.isEmpty(application.getServicesMatchedToProvider())) {
            for (Service service : application.getServicesMatchedToProvider()) {
                deployment.readCredentials(application.getName(), service.getServiceName(), service.getServiceType());
            }
        }
    }

    /**
     looks for a suitable service of the provider which matches to the needed service
     adds the creation command to the deployscript

     @param deployScript script the commands will be written in
     */
    private void handleServices(BashScript deployScript) throws IOException {
        for (Application application : applications) {
            Deployment deployment = new Deployment(deployScript, application, fileAccess);

            //only one time all service offerings should be printed to the deploy script
            deployment.treatServices();
        }
    }

    /**
     replaces strings in files with suitable strings.
     If a path is not suitable to the path in the warden container
     A replace command will be added to the deployscript which replaces the Strings locally.

     @param deployScript script the commands will be written in
     */
    private void replaceStrings(BashScript deployScript) throws IOException {
        for (Application application : applications) {
            Deployment deployment = new Deployment(deployScript, application, fileAccess);
            if (!application.getExecuteCommands().isEmpty()) {
                Map<String, String> executeCommands = application.getExecuteCommands();

                for (Map.Entry<String, String> command : executeCommands.entrySet()) {
                    //TODO: add lists which strings should be replaced. In 12 Factor it is probably not necessary.
                    deployment.replaceStrings(command.getKey(), "/var/www/html/", "/home/vcap/app/htdocs/");
                }
            }
        }
    }

    /**
     detect if additional buildpacks are needed and add them
     */
    private void createBuildpackAdditionsFile(Application application) throws IOException, JSONException {
        BuildpackDetector buildpackDetection = new BuildpackDetector(application, fileAccess);
        buildpackDetection.detectBuildpackAdditions();
    }

    private void createAttributes(Application application) throws IOException {

        if (!application.getAttributes().isEmpty()) {
            ArrayList<String> attributes = new ArrayList<>();

            for (Map.Entry<String, String> attribute : application.getAttributes().entrySet()) {
                attributes.add(String.format("  %s: %s", attribute.getKey(), attribute.getValue()));
            }

            if (!application.getAttributes().containsKey(DOMAIN.getName())) {
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

    private void insertFiles(Application application) throws IOException {
        for (String filePath : application.getFilePaths()) {
            String path = APPLICATION_FOLDER + application.getApplicationNumber() + "/" + filePath;
            fileAccess.copy(filePath, path);
        }
    }
}
