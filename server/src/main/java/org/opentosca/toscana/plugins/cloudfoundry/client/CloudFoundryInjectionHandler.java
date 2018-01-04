package org.opentosca.toscana.plugins.cloudfoundry.client;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.plugins.cloudfoundry.application.CloudFoundryApplication;
import org.opentosca.toscana.plugins.cloudfoundry.application.CloudFoundryService;
import org.opentosca.toscana.plugins.cloudfoundry.application.CloudFoundryServiceType;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 inject service credentials to the environment variables
 depends on the service type
 */
public class CloudFoundryInjectionHandler {

    private final static Logger logger = LoggerFactory.getLogger(CloudFoundryInjectionHandler.class);
    private CloudFoundryApplication app;
    private CloudFoundryConnection cloudFoundryConnection;
    private PluginFileAccess fileAccess;

    public CloudFoundryInjectionHandler(PluginFileAccess fileAccess, CloudFoundryApplication app) {
        this.app = app;
        this.cloudFoundryConnection = app.getConnection();
        this.fileAccess = fileAccess;
    }

    /**
     create services on CF instance, deploy application, bind application to service and
     add the credentials to the environment variables.
     */
    public void injectServiceCredentials() {
        deploy();
        getServiceCredentials();
    }

    /**
     deploys the application, creates the services and bind the application to the service
     the application is not running after that
     */
    public boolean deploy() {
        Boolean succeed = false;
        try {
            if (cloudFoundryConnection != null) {
                Path pathToApplication = Paths.get(fileAccess.getAbsolutePath(app.getPathToApplication()));
                succeed = cloudFoundryConnection.pushApplication(pathToApplication,
                    app.getName(), app.getServicesMatchedToProvider());
            }
        } catch (InterruptedException e) {
            logger.error("Something went wrong while pushing the application", e);
        } catch (FileNotFoundException e) {
            logger.error("Something went wrong while pushing the application", e);
        }
        return succeed;
    }

    /**
     read the service credentials of the services which is binded to the application
     adds the value of the credentials to the environment variable
     //TODO: expand with more ServiceTypes
     */
    public void getServiceCredentials() {

        for (CloudFoundryService service : app.getServicesMatchedToProvider()) {
            if (service.getServiceType() == CloudFoundryServiceType.MYSQL) {
                try {
                    String port = cloudFoundryConnection.getServiceCredentials(service.getServiceName(),
                        app.getName()).getString("port");
                    String username = cloudFoundryConnection.getServiceCredentials(service.getServiceName(),
                        app.getName()).getString("username");
                    String database_name = cloudFoundryConnection.getServiceCredentials(service.getServiceName(),
                        app.getName()).getString("name");
                    String password = cloudFoundryConnection.getServiceCredentials(service.getServiceName(),
                        app.getName()).getString("password");
                    String host = cloudFoundryConnection.getServiceCredentials(service.getServiceName(),
                        app.getName()).getString("hostname");

                    //TODO: check for environment variable names. Probably in the ToscaSpec
                    app.addEnvironmentVariables("database_user", username);
                    app.addEnvironmentVariables("database_name", database_name);
                    app.addEnvironmentVariables("database_host", host);
                    app.addEnvironmentVariables("database_password", password);
                    app.addEnvironmentVariables("database_port", port);
                } catch (JSONException | JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
