package org.opentosca.toscana.plugins.cloudfoundry.application.buildpacks;

import java.io.IOException;
import java.util.Arrays;

import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryPlugin;
import org.opentosca.toscana.plugins.cloudfoundry.application.Application;
import org.opentosca.toscana.plugins.cloudfoundry.application.ServiceTypes;
import org.opentosca.toscana.plugins.util.TransformationFailureException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.opentosca.toscana.plugins.cloudfoundry.FileCreator.APPLICATION_FOLDER;

/**
 detect which language is used and add additional buildpacks which are not default
 checks the combination of used services and language
 */
public class BuildpackDetector {

    public static final String BUILDPACK_OBJECT_PHP = "PHP_EXTENSIONS";
    public static final String BUILDPACK_FILEPATH_PHP = ".bp-config/options.json";

    private final static Logger logger = LoggerFactory.getLogger(CloudFoundryPlugin.class);

    private Application application;
    private String applicationSuffix;
    private PluginFileAccess fileAccess;

    public BuildpackDetector(Application application, PluginFileAccess fileAccess) {
        this.application = application;
        this.fileAccess = fileAccess;
        this.applicationSuffix = application.getApplicationSuffix();
    }

    public void detectBuildpackAdditions() {
        if (applicationSuffix != null) {
            logger.info("Application suffix is: " + applicationSuffix);
            if (applicationSuffix.equalsIgnoreCase("php")) {
                try {
                    addBuildpackAdditonsPHP();
                } catch (JSONException | IOException e) {
                    throw new TransformationFailureException("Fail to add buildpacks", e);
                }

                //TODO: expand with more languages
            }
        }
    }

    private void addBuildpackAdditonsPHP() throws JSONException, IOException {

        if (application.getServices().containsValue(ServiceTypes.MYSQL)) {
            JSONObject buildPackAdditionsJson = new JSONObject();
            JSONArray buildPacks = new JSONArray(Arrays.asList("mysql", "mysqli"));

            //add default php values because they will be overriden by manual additions
            for (String buildpack : Buildpacks.DEFAULT_PHP_BUILDPACKS) {
                buildPacks.put(buildpack);
            }

            buildPackAdditionsJson.put(BUILDPACK_OBJECT_PHP, buildPacks);
            String path;
            if (application.getPathToApplication() != null) {
                path = String.format("%s%s/%s/%s", APPLICATION_FOLDER, application.getApplicationNumber(), application.getPathToApplication(), BUILDPACK_FILEPATH_PHP);
            } else {
                path = BUILDPACK_FILEPATH_PHP;
            }
            fileAccess.access(path).append(buildPackAdditionsJson.toString(4)).close();
        }
    }
}
