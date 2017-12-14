package org.opentosca.toscana.plugins.cloudfoundry;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.visitor.VisitableNode;
import org.opentosca.toscana.plugins.cloudfoundry.application.CloudFoundryApplication;
import org.opentosca.toscana.plugins.cloudfoundry.application.CloudFoundryProvider;
import org.opentosca.toscana.plugins.cloudfoundry.cloudFoundryClient.CloudFoundryConnection;
import org.opentosca.toscana.plugins.cloudfoundry.visitors.CloudFoundryNodeVisitor;
import org.opentosca.toscana.plugins.lifecycle.AbstractLifecycle;

import org.json.JSONException;

public class CloudFoundryLifecycle extends AbstractLifecycle {

    private CloudFoundryProvider provider;

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
            String username = properties.get("username");
            String password = properties.get("password");
            String organization = properties.get("organization");
            String space = properties.get("space");
            String apiHost = properties.get("apihost");

            CloudFoundryConnection cloudFoundryConnection = new CloudFoundryConnection(username, password,
                apiHost, organization, space);

            //TODO: check how to get used provider or figure out whether it is necessary to know it?
            provider = new CloudFoundryProvider(CloudFoundryProvider.CloudFoundryProviderType.PIVOTAL);
            provider.setOfferedService(cloudFoundryConnection.getServices());
        }
    }

    @Override
    public void transform() {
        CloudFoundryApplication myApp = new CloudFoundryApplication();
        myApp.setProvider(provider);
        CloudFoundryNodeVisitor visitor = new CloudFoundryNodeVisitor(myApp);
        Set<RootNode> nodes = context.getModel().getNodes();

        for (VisitableNode node : nodes) {
            node.accept(visitor);
        }
        myApp = visitor.getFilledApp();

        try {
            CloudFoundryFileCreator fileCreator = new CloudFoundryFileCreator(context.getPluginFileAccess(), myApp);
            fileCreator.createFiles();
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

