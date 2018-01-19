package org.opentosca.toscana.plugins.cloudfoundry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.visitor.VisitableNode;
import org.opentosca.toscana.plugins.cloudfoundry.application.Application;
import org.opentosca.toscana.plugins.cloudfoundry.application.Provider;
import org.opentosca.toscana.plugins.cloudfoundry.client.Connection;
import org.opentosca.toscana.plugins.cloudfoundry.visitors.NodeVisitors;
import org.opentosca.toscana.plugins.lifecycle.AbstractLifecycle;

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
    public void prepare() {
        Map<String, String> properties = context.getProperties().getPropertyValues();

        if (!properties.isEmpty()) {
            String username = properties.get(CF_PROPERTY_KEY_USERNAME);
            String password = properties.get(CF_PROPERTY_KEY_PASSWORD);
            String organization = properties.get(CF_PROPERTY_KEY_ORGANIZATION);
            String space = properties.get(CF_PROPERTY_KEY_SPACE);
            String apiHost = properties.get(CF_PROPERTY_KEY_API);

            if (isNotNull(username, password, organization, space, apiHost)) {

                connection = new Connection(username, password,
                    apiHost, organization, space);

                //TODO: check how to get used provider or figure out whether it is necessary to know it?
                provider = new Provider(Provider.CloudFoundryProviderType.PIVOTAL);
                provider.setOfferedService(connection.getServices());
            }
        }

        //TODO: check how many different applications there are and fill list with them
        //probably there must be a combination of application and set of nodes
        applications = new ArrayList<>();
        Application myApp = new Application();
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
        PluginFileAccess fileAccess = context.getPluginFileAccess();
        Set<RootNode> nodes = context.getModel().getNodes();
        List<Application> filledApplications = new ArrayList<>();
        for (Application application : applications) {
            NodeVisitors visitor = new NodeVisitors(application);
            for (VisitableNode node : nodes) {
                node.accept(visitor);
            }

            Application filledApplication = visitor.getFilledApp();
            filledApplications.add(filledApplication);
        }

        try {
            FileCreator fileCreator = new FileCreator(fileAccess, filledApplications);
            fileCreator.createFiles();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cleanup() {
        //throw new UnsupportedOperationException();
    }
}

