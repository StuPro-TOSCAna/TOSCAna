package org.opentosca.toscana.plugins.cloudfoundry.client;

import java.nio.file.Paths;

import org.opentosca.toscana.plugins.cloudfoundry.application.CloudFoundryApplication;

/**
 Created by jensmuller on 02.01.18.
 */
public class CloudFoundryInjectionHandler {

    private CloudFoundryApplication app;
    private CloudFoundryConnection cloudFoundryConnection;

    public CloudFoundryInjectionHandler(CloudFoundryApplication app) {
        this.app = app;
        this.cloudFoundryConnection = app.getConnection();
    }

    //TODO: deploy the application to create service and bind the application to it
    //needs a manifest to deploy
    private void deploy() {
        cloudFoundryConnection.pushApplication(Paths.get(app.getPathToApplication()),
            app.getName(), "cleardb", "spark", "my_db");
    }

    //TODO: read the service credentials and add it to the environment variables which the values belong to
    //depends on the service type
    private void getServiceCredentials() {

    }

    //TODO: stop (or delete?) the pre deployed application
    private void stopApplicationInstance() {

    }

    //TODO: delete the old manifest
    private void deleteOldManifest() {

    }

    //TODO: create the new manifest with the values of the service credentials
    private void createNewManifest() {

    }
}
