package org.opentosca.toscana.plugins.cloudfoundry.application.buildpacks;

import java.io.IOException;
import java.util.Arrays;

import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryPlugin;
import org.opentosca.toscana.plugins.cloudfoundry.application.CloudFoundryApplication;
import org.opentosca.toscana.plugins.cloudfoundry.application.CloudFoundryServiceType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 detect which language is used and add additional buildpacks which are not default
 checks the combination of used services and language
 */
public class CloudFoundryBuildpackDetection {

    public static final String BUILDPACK_OBJECT_PHP = "PHP_EXTENSIONS";
    public static final String BUILDPACK_FILEPATH_PHP = ".bp-config/options.json";

    private final static Logger logger = LoggerFactory.getLogger(CloudFoundryPlugin.class);

    private CloudFoundryApplication application;
    private String applicationSuffix;
    private PluginFileAccess fileAccess;

    public CloudFoundryBuildpackDetection(CloudFoundryApplication application, PluginFileAccess fileAccess) {
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
                    logger.error("Something went wrong while trying to add additional buildpacks", e);
                }

                //TODO: expand with more languages
            }
        }
    }

    private void addBuildpackAdditonsPHP() throws JSONException, IOException {

        if (application.getServices().containsValue(CloudFoundryServiceType.MYSQL)) {
            JSONObject buildPackAdditionsJson = new JSONObject();
            JSONArray buildPacks = new JSONArray(Arrays.asList("mysql", "mysqli"));

            //add default php values because they will be overriden by manual additions
            for (String buildpack : CloudFoundryBuildpack.DEFAULT_PHP_BUILDPACKS) {
                buildPacks.put(buildpack);
            }

            buildPackAdditionsJson.put(BUILDPACK_OBJECT_PHP, buildPacks);
            String path;
            if (application.getPathToApplication() != null) {
                path = String.format("%s/%s", application.getPathToApplication(), BUILDPACK_FILEPATH_PHP);
            } else {
                path = BUILDPACK_FILEPATH_PHP;
            }
            fileAccess.access(path).append(buildPackAdditionsJson.toString(4)).close();
        }
    }
}
