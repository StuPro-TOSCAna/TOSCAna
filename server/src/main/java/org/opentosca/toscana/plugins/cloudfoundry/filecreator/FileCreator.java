package org.opentosca.toscana.plugins.cloudfoundry.filecreator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.plugins.cloudfoundry.application.Application;
import org.opentosca.toscana.plugins.cloudfoundry.application.Service;
import org.opentosca.toscana.plugins.cloudfoundry.application.ServiceTypes;
import org.opentosca.toscana.plugins.cloudfoundry.application.buildpacks.BuildpackDetector;
import org.opentosca.toscana.plugins.cloudfoundry.application.deployment.Deployment;
import org.opentosca.toscana.plugins.scripts.BashScript;
import org.opentosca.toscana.plugins.scripts.EnvironmentCheck;

import org.apache.commons.collections.CollectionUtils;
import org.json.JSONException;
import org.slf4j.Logger;

import static org.opentosca.toscana.core.plugin.lifecycle.AbstractLifecycle.OUTPUT_DIR;
import static org.opentosca.toscana.core.plugin.lifecycle.AbstractLifecycle.SCRIPTS_DIR_PATH;
import static org.opentosca.toscana.plugins.cloudfoundry.application.ManifestAttributes.DOMAIN;
import static org.opentosca.toscana.plugins.cloudfoundry.application.ManifestAttributes.ENVIRONMENT;
import static org.opentosca.toscana.plugins.cloudfoundry.application.ManifestAttributes.NO_ROUTE;
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
    public static final String CLI_NO_START = " --no-start";
    public static final String CLI_START = "cf start ";
    public static final String CLI_PATH_TO_MANIFEST = " -f ../";
    public static final String FILEPRAEFIX_DEPLOY = "deploy_";
    public static final String FILESUFFIX_DEPLOY = ".sh";
    public static final String APPLICATION_FOLDER = "app";
    public static final String ENVIRONMENT_CONFIG_FILE = "_environment_config.txt";
    public static String deploy_name = "application";

    private TransformationContext context;
    private Logger logger;
    private List<String> seenConfiguredServices = new ArrayList<>();
    
    // this list contains services which are already matched to a service of a provider and already created.
    private List<String> alreadyHandledServices = new ArrayList<>();

    private final PluginFileAccess fileAccess;
    private List<Application> applications;

    public FileCreator(PluginFileAccess fileAccess, List<Application> applications, TransformationContext context) {
        this.fileAccess = fileAccess;

        //check applications and remove dummy applications
        this.applications = new ApplicationHandler(applications).handleApplications();
        this.logger = context.getLogger(getClass());
        this.context = context;
    }

    /**
     creates all files which are necessary for deployment
     Each application has to be filled with information
     */
    public void createFiles() throws IOException, JSONException {
        logger.info("Create manifest.yml");
        createManifest();

        logger.info("Create deploy script");
        createDeployScript();

        for (Application application : applications) {
            createBuildpackAdditionsFile(application);
            insertFiles(application);
            createEnvironmentConfigFile(application, application.getEnvironmentVariables());
        }
    }

    /**
     creates the manifest
     multiple applications are in the same manifest
     */
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

    /**
     creates once the manifest header
     */
    private void createManifestHead() throws IOException {
        String manifestHead = String.format(MANIFESTHEAD);
        fileAccess.access(MANIFEST_PATH).appendln(manifestHead).close();
    }

    /**
     add the name block to manifest.
     after this all application information will be inserted
     */
    private void addNameToManifest(Application application) throws IOException {
        String nameBlock = String.format("- %s: %s", NAMEBLOCK, application.getName());
        fileAccess.access(MANIFEST_PATH).appendln(nameBlock).close();
    }

    /**
     adds the relative path of the application folder to the manifest
     */
    private void addPathToApplication(Application application) throws IOException {

        String pathAddition = String.format("  %s: ../%s", PATH.getName(),
            APPLICATION_FOLDER + application.getApplicationNumber());

        if (application.isEnablePathToApplication()) {
            pathAddition = String.format("%s/%s", pathAddition, application.getPathToApplication());
        }

        logger.info("Add path to application {} to manifest", pathAddition);
        fileAccess.access(MANIFEST_PATH).appendln(pathAddition).close();
    }

    /**
     creates a environment section in the manifest and adds all setted environments variables to it
     if there is an empty environment variable a default value will be added
     */
    private void createEnvironmentVariables(Application application) throws IOException {
        Map<String, String> envVariables = application.getEnvironmentVariables();
        if (!envVariables.isEmpty()) {
            ArrayList<String> environmentVariables = new ArrayList<>();
            environmentVariables.add(String.format("  %s:", ENVIRONMENT.getName()));
            for (Map.Entry<String, String> entry : envVariables.entrySet()) {
                environmentVariables.add(String.format("    %s: %s", entry.getKey(), entry.getValue()));
                logger.debug("Add environment variable {} value: {} to manifest", entry.getKey(), entry.getValue());
            }
            for (String env : environmentVariables) {
                fileAccess.access(MANIFEST_PATH).appendln(env).close();
            }
        }
    }

    /**
     creates a environment config file which contains all environment variables.
     this file will be read by a python script

     @param application          the current application. Each application gets its own file
     @param environmentVariables all environment variables of the current application
     */
    private void createEnvironmentConfigFile(Application application, Map<String, String> environmentVariables) throws IOException {
        String applicationName = application.getName();
        String dictEnv = "{";

        for (Map.Entry<String, String> env : environmentVariables.entrySet()) {
            dictEnv = String.format("%s \'%s\':\'%s\', ", dictEnv, env.getValue(), env.getKey());
        }
        dictEnv = dictEnv + "}";

        logger.info("Create environment config file {}", SCRIPTS_DIR_PATH + applicationName + ENVIRONMENT_CONFIG_FILE);
        fileAccess.access(SCRIPTS_DIR_PATH + applicationName + ENVIRONMENT_CONFIG_FILE).append(dictEnv).close();
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
                logger.debug("Add service {} to manifest", service.getKey());
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
        logger.debug("Handle services");
        handleServices(deployScript);

        //replace
        logger.debug("Replace strings");
        replaceStrings(deployScript);

        //push applications
        for (Application application : applications) {
            deployScript.append(CLI_PUSH + application.getName() + CLI_PATH_TO_MANIFEST + MANIFEST_NAME + CLI_NO_START);
        }

        //read credentials, replace, executeScript, configureMysql
        for (Application application : applications) {
            Deployment deployment = new Deployment(deployScript, application, fileAccess, context);

            //read credentials
            readCredentials(deployment, application);

            //configureSql
            configureSql(deployment, application);

            //start application
            deployScript.append(CLI_START + application.getName());

            //execute
            executeFiles(deployment, application);
        }
    }

    /**
     the files in the application which are signed as sql and config scripts will be executed in the database
     */
    private void configureSql(Deployment deployment, Application application) throws IOException {
        if (!application.getConfigMysql().isEmpty()) {

            for (Map.Entry<String, String> entry : application.getConfigMysql().entrySet()) {
                if (!seenConfiguredServices.contains(entry.getKey())) {
                    deployment.configureSql(entry.getValue());
                    seenConfiguredServices.add(entry.getKey());
                } else {
                    logger.debug("Do not add the configure sql file twice. The database is already configured by another command");
                }
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
                deployment.readCredentials(application.getName(), service.getServiceName(), service.getServiceType(), service.getServiceInstanceName());
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
            Deployment deployment = new Deployment(deployScript, application, fileAccess, context);

            //only one time all service offerings should be printed to the deploy script
            this.alreadyHandledServices = deployment.treatServices(alreadyHandledServices);
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
            Deployment deployment = new Deployment(deployScript, application, fileAccess, context);
            if (!application.getExecuteCommands().isEmpty()) {
                Map<String, String> executeCommands = application.getExecuteCommands();

                for (Map.Entry<String, String> command : executeCommands.entrySet()) {
                    //TODO: add lists which strings should be replaced. In 12 Factor it is probably not necessary.
                    String findStr = "/var/www/html/";
                    String replaceStr = "/home/vcap/app/htdocs/";
                    deployment.replaceStrings(command.getKey(), findStr, replaceStr);
                    logger.info("Add command to replace all occurence of {} with {}", findStr, replaceStr);
                }
            }
        }
    }

    /**
     detect if additional buildpacks are needed and add them
     */
    private void createBuildpackAdditionsFile(Application application) throws IOException, JSONException {
        BuildpackDetector buildpackDetection = new BuildpackDetector(application, fileAccess, context);
        buildpackDetection.detectBuildpackAdditions();
    }

    /**
     fills in the attributes of the application
     default is a random route attribute to avoid a deployment failure because the route to the app already exists
     */
    private void createAttributes(Application application) throws IOException {

        if (!application.getAttributes().isEmpty()) {
            ArrayList<String> attributes = new ArrayList<>();

            for (Map.Entry<String, String> attribute : application.getAttributes().entrySet()) {
                attributes.add(String.format("  %s: %s", attribute.getKey(), attribute.getValue()));
            }

            if (!application.getAttributes().containsKey(DOMAIN.getName())
                && !application.getAttributes().containsKey(NO_ROUTE.getName())) {
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

    /**
     insert all setted files to the application folder
     */
    private void insertFiles(Application application) throws IOException {
        for (String filePath : application.getFilePaths()) {
            String path = APPLICATION_FOLDER + application.getApplicationNumber() + "/" + filePath;
            logger.debug("Copy file {} to {}", filePath, path);
            fileAccess.copy(filePath, path);
        }
    }
}
