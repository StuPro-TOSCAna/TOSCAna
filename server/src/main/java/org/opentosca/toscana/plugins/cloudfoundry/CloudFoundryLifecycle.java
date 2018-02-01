package org.opentosca.toscana.plugins.cloudfoundry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.plugin.lifecycle.AbstractLifecycle;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.properties.NoSuchPropertyException;
import org.opentosca.toscana.core.transformation.properties.PropertyInstance;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.visitor.VisitableNode;
import org.opentosca.toscana.plugins.cloudfoundry.application.Application;
import org.opentosca.toscana.plugins.cloudfoundry.application.Provider;
import org.opentosca.toscana.plugins.cloudfoundry.client.Connection;
import org.opentosca.toscana.plugins.cloudfoundry.visitors.NodeVisitor;
import org.opentosca.toscana.plugins.util.TransformationFailureException;

import org.json.JSONException;

import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryPlugin.CF_PROPERTY_KEY_API;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryPlugin.CF_PROPERTY_KEY_ORGANIZATION;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryPlugin.CF_PROPERTY_KEY_PASSWORD;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryPlugin.CF_PROPERTY_KEY_SPACE;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryPlugin.CF_PROPERTY_KEY_USERNAME;

public class CloudFoundryLifecycle extends AbstractLifecycle {

    private Provider provider;
    private Connection connection;
    private List<Application> applications;

    public CloudFoundryLifecycle(TransformationContext context) throws IOException {
        super(context);
    }

    @Override
    public boolean checkModel() {
        //throw new UnsupportedOperationException();
        return true;
    }

    @Override
    public void prepare() throws NoSuchPropertyException {
        PropertyInstance properties = context.getProperties();

        if (!properties.isEmpty()) {
            String username = properties.get(CF_PROPERTY_KEY_USERNAME).orElse(null);
            String password = properties.get(CF_PROPERTY_KEY_PASSWORD).orElse(null);
            String organization = properties.get(CF_PROPERTY_KEY_ORGANIZATION).orElse(null);
            String space = properties.get(CF_PROPERTY_KEY_SPACE).orElse(null);
            String apiHost = properties.get(CF_PROPERTY_KEY_API).orElse(null);

            if (isNotNull(username, password, organization, space, apiHost)) {

                connection = new Connection(username, password,
                    apiHost, organization, space, context);

                //TODO: check how to get used provider or figure out whether it is necessary to know it?
                provider = new Provider(Provider.CloudFoundryProviderType.PIVOTAL);
                provider.setOfferedService(connection.getServices());
            }
        }

        //TODO: check how many different applications there are and fill list with them
        //probably there must be a combination of application and set of nodes
        applications = new ArrayList<>();
        Application myApp = new Application(1, context);
        myApp.setProvider(provider);
        myApp.setConnection(connection);
        applications.add(myApp);
    }

    private boolean isNotNull(String... elements) {
        for (String el : elements) {
            if (el == null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void transform() {
        Application myApp = new Application(1, context);
        PluginFileAccess fileAccess = context.getPluginFileAccess();
        Set<RootNode> nodes = context.getModel().getNodes();
        List<Application> filledApplications = new ArrayList<>();
        for (Application application : applications) {
            NodeVisitor visitor = new NodeVisitor(application);
            for (VisitableNode node : nodes) {
                node.accept(visitor);
            }

            Application filledApplication = visitor.getFilledApp();
            filledApplications.add(filledApplication);
        }

        try {
            FileCreator fileCreator = new FileCreator(fileAccess, filledApplications, context);
            fileCreator.createFiles();
        } catch (IOException | JSONException e) {
            throw new TransformationFailureException("Something went wrong while creating the output files", e);
        }
    }

    @Override
    public void cleanup() {
        //throw new UnsupportedOperationException();
    }
}

