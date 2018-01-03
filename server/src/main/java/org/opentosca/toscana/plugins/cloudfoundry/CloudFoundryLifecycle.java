package org.opentosca.toscana.plugins.cloudfoundry;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.visitor.VisitableNode;
import org.opentosca.toscana.plugins.cloudfoundry.application.CloudFoundryApplication;
import org.opentosca.toscana.plugins.cloudfoundry.application.CloudFoundryProvider;
import org.opentosca.toscana.plugins.cloudfoundry.client.CloudFoundryConnection;
import org.opentosca.toscana.plugins.cloudfoundry.client.CloudFoundryInjectionHandler;
import org.opentosca.toscana.plugins.cloudfoundry.visitors.CloudFoundryNodeVisitor;
import org.opentosca.toscana.plugins.lifecycle.AbstractLifecycle;

import org.json.JSONException;

import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryPlugin.CF_PROPERTY_KEY_API;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryPlugin.CF_PROPERTY_KEY_ORGANIZATION;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryPlugin.CF_PROPERTY_KEY_PASSWORD;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryPlugin.CF_PROPERTY_KEY_SPACE;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryPlugin.CF_PROPERTY_KEY_USERNAME;

public class CloudFoundryLifecycle extends AbstractLifecycle {

    private CloudFoundryProvider provider;
    private CloudFoundryConnection cloudFoundryConnection;

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
        //throw new UnsupportedOperationException();

        Map<String, String> properties = context.getProperties().getPropertyValues();

        if (!properties.isEmpty()) {
            String username = properties.get(CF_PROPERTY_KEY_USERNAME);
            String password = properties.get(CF_PROPERTY_KEY_PASSWORD);
            String organization = properties.get(CF_PROPERTY_KEY_ORGANIZATION);
            String space = properties.get(CF_PROPERTY_KEY_SPACE);
            String apiHost = properties.get(CF_PROPERTY_KEY_API);

            if (username != null &&
                password != null &&
                organization != null &&
                space != null &&
                apiHost != null) {

                cloudFoundryConnection = new CloudFoundryConnection(username, password,
                    apiHost, organization, space);

                //TODO: check how to get used provider or figure out whether it is necessary to know it?
                provider = new CloudFoundryProvider(CloudFoundryProvider.CloudFoundryProviderType.PIVOTAL);
                provider.setOfferedService(cloudFoundryConnection.getServices());
            }
        }
    }

    @Override
    public void transform() {
        CloudFoundryApplication myApp = new CloudFoundryApplication();
        myApp.setProvider(provider);
        myApp.setConnection(cloudFoundryConnection);
        CloudFoundryNodeVisitor visitor = new CloudFoundryNodeVisitor(myApp);
        Set<RootNode> nodes = context.getModel().getNodes();

        for (VisitableNode node : nodes) {
            node.accept(visitor);
        }
        myApp = visitor.getFilledApp();

        try {
            CloudFoundryFileCreator fileCreator = new CloudFoundryFileCreator(context.getPluginFileAccess(), myApp);
            fileCreator.createFiles();
            if (cloudFoundryConnection != null) {
                CloudFoundryInjectionHandler injectionHandler = new CloudFoundryInjectionHandler(myApp);
                injectionHandler.injectServiceCredentials();
                fileCreator.updateManifest();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cleanup() {
        //throw new UnsupportedOperationException();
    }
}

