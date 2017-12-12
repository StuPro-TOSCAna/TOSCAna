package org.opentosca.toscana.plugins.cloudfoundry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.cloudfoundry.operations.services.ServiceOffering;
import org.cloudfoundry.operations.services.ServicePlan;
import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.plugins.scripts.BashScript;
import org.opentosca.toscana.plugins.scripts.EnvironmentCheck;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryManifestAttribute.ENVIRONMENT;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryManifestAttribute.SERVICE;

/**
 * Creates all files which are necessary to deploy the application
 * Files: manifest.yml, builpack additions, deployScript
 */
public class CloudFoundryFileCreator {

    public static final String MANIFEST = "manifest.yml";
    public static final String MANIFESTHEAD = "---\napplications:\n";
    public static final String NAMEBLOCK = "name";
    public static final String CLI_CREATE_SERVICE_DEFAULT = "cf create-service {plan} {service} ";
    public static final String CLI_CREATE_SERVICE = "cf create-service ";
    public static final String CLI_PUSH = "cf push ";
    public static final String FILEPRAEFIX_DEPLOY = "deploy_";
    public static final String FILESUFFIX_DEPLOY = ".sh";
    public static final String BUILDPACK_OBJECT_PHP = "PHP-EXTENSIONS";
    public static final String BUILDPACK_FILEPATH_PHP = ".bp-config/options.json";

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
        insertFiles();
    }

    private void createManifest() throws IOException {
        createManifestHead();
        createAttributes();
        createEnvironmentVariables();
        createService();
    }

    private void createManifestHead() throws IOException {
        String manifestHead = String.format("%s- %s: %s", MANIFESTHEAD, NAMEBLOCK, app.getName());
        fileAccess.access(MANIFEST).appendln(manifestHead).close();
    }

    private void createEnvironmentVariables() throws IOException {
        ArrayList<String> environmentVariables = new ArrayList<>();
        environmentVariables.add(String.format("  %s:", ENVIRONMENT.getName()));
        for (Map.Entry<String, String> entry : app.getEnvironmentVariables().entrySet()) {
            environmentVariables.add(String.format("    %s: %s", entry.getKey(), entry.getValue()));
        }
        for (String env : environmentVariables) {
            fileAccess.access(MANIFEST).appendln(env).close();
        }
    }

    /**
     * creates a service which depends on the template
     * add it to the manifest
     *
     * @throws IOException
     */
    private void createService() throws IOException {
        ArrayList<String> services = new ArrayList<>();
        services.add(String.format("  %s:", SERVICE.getName()));
        for (Map.Entry<String, String> service : app.getServices().entrySet()) {
            services.add(String.format("    - %s", service.getKey()));
        }
        for (String service : services) {
            fileAccess.access(MANIFEST).appendln(service).close();
        }
    }

    /**
     * creates a deploy shell script
     *
     * @throws IOException
     */
    private void createDeployScript() throws IOException {
        BashScript deployScript = new BashScript(fileAccess, FILEPRAEFIX_DEPLOY + app.getName());
        deployScript.append(EnvironmentCheck.checkEnvironment("cf"));

        if (app.getProvider() != null) {
            addProviderServiceOfferings(deployScript);
            for (Map.Entry<String, String> service : app.getServices().entrySet()) {
                String description = service.getValue();
                CloudFoundryProvider provider = app.getProvider();
                List<ServiceOffering> services = provider.getOfferedService();
                Boolean isSet = false;

                //checks if a offered service of the provider contains the description of the needed service
                //if yes then add the service to the script with a free plan
                for (ServiceOffering offeredService : services) {
                    if (offeredService.getDescription().toLowerCase().indexOf(description.toLowerCase()) != -1) {
                        for (ServicePlan plan : offeredService.getServicePlans()) {
                            if (plan.getFree()) {
                                deployScript.append(String.format("%s %s %s %s", CLI_CREATE_SERVICE,
                                    offeredService.getLabel(), plan.getName(), service.getKey()));
                                isSet = true;
                                break;
                            }
                        }
                    }
                }

                if (!isSet) {
                    deployScript.append(CLI_CREATE_SERVICE_DEFAULT + service);
                }
            }
        } else {
            for (Map.Entry<String, String> service : app.getServices().entrySet()) {
                deployScript.append(CLI_CREATE_SERVICE_DEFAULT + service.getKey());
            }
        }
        
        deployScript.append(CLI_PUSH + app.getName());
    }

    //only for PHP
    private void createBuildpackAdditionsFile() throws IOException, JSONException {
        JSONObject buildPackAdditionsJson = new JSONObject();
        JSONArray buildPacks = new JSONArray();
        for (String buildPack : app.getBuildpackAdditions()) {
            buildPacks.put(buildPack);
        }
        buildPackAdditionsJson.put(BUILDPACK_OBJECT_PHP, buildPacks);
        fileAccess.access(BUILDPACK_FILEPATH_PHP).append(buildPackAdditionsJson.toString(4)).close();
    }

    private void createAttributes() throws IOException {

        if (!app.getAttributes().isEmpty()) {
            ArrayList<String> attributes = new ArrayList<>();
            for (Map.Entry<String, String> attribute : app.getAttributes().entrySet()) {
                attributes.add(String.format("  %s: %s", attribute.getKey(), attribute.getValue()));
            }
            for (String attribute : attributes) {
                fileAccess.access(MANIFEST).appendln(attribute).close();
            }
        }
    }

    private void insertFiles() throws IOException {
        for (String filePath : app.getFilePaths()) {
            fileAccess.copy(filePath);
        }
    }

    private void addProviderServiceOfferings(BashScript deployScript) throws IOException {
        CloudFoundryProvider provider = app.getProvider();
        List<ServiceOffering> services = provider.getOfferedService();

        deployScript.append("# following services you could choose:");
        for (ServiceOffering service : services) {
            String plans = "";
            for (ServicePlan plan : service.getServicePlans()) {
                String currentPlan;
                if (plan.getFree()) {
                    currentPlan = plan.getName();
                } else {
                    currentPlan = plan.getName() + "*";
                }

                plans = String.format("%s, %s, ", plans, currentPlan);
            }
            deployScript.append(String.format("# %s \t %s \t %s ", service.getLabel(), plans, service.getDescription()));
        }
        deployScript.append("\n* These service plans have an associated cost. Creating a service instance will incur this cost.");
    }

}
