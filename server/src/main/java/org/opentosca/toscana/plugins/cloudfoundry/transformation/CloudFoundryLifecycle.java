package org.opentosca.toscana.plugins.cloudfoundry.transformation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.plugin.lifecycle.AbstractLifecycle;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.plugins.cloudfoundry.FileCreator;
import org.opentosca.toscana.plugins.cloudfoundry.application.Application;
import org.opentosca.toscana.plugins.cloudfoundry.application.Provider;
import org.opentosca.toscana.plugins.cloudfoundry.client.Connection;
import org.opentosca.toscana.plugins.cloudfoundry.transformation.sort.CloudFoundryNode;
import org.opentosca.toscana.plugins.cloudfoundry.transformation.sort.CloudFoundryStack;
import org.opentosca.toscana.plugins.cloudfoundry.transformation.sort.GraphSort;
import org.opentosca.toscana.plugins.cloudfoundry.transformation.visitors.ComputeNodeFinder;
import org.opentosca.toscana.plugins.cloudfoundry.transformation.visitors.NodeSupported;
import org.opentosca.toscana.plugins.cloudfoundry.transformation.visitors.NodeVisitor;

import org.json.JSONException;

import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryPlugin.CF_PROPERTY_KEY_API;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryPlugin.CF_PROPERTY_KEY_ORGANIZATION;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryPlugin.CF_PROPERTY_KEY_PASSWORD;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryPlugin.CF_PROPERTY_KEY_SPACE;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryPlugin.CF_PROPERTY_KEY_USERNAME;

public class CloudFoundryLifecycle extends AbstractLifecycle {

    private final EffectiveModel model;
    private Provider provider;
    private Connection connection;
    private List<Application> applications;
    private Map<String, CloudFoundryNode> applicationNodes = new HashMap<>();
    private Set<CloudFoundryNode> computeNodes = new HashSet<>();
    private Set<CloudFoundryStack> stacks = new HashSet<>();

    public CloudFoundryLifecycle(TransformationContext context) throws IOException {
        super(context);
        model = context.getModel();

        Map<String, String> properties = context.getProperties().getPropertyValues();

        logger.debug("Checking for Properties");
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
    }

    @Override
    public boolean checkModel() {
        logger.info("Begin check for supported Node Types.");
        Set<RootNode> nodes = model.getNodes();
        return checkNodeTypes(nodes);
    }

    /**
     checks a Set of Nodes for support of its Type

     @param nodes the set which gets checked
     @return true if supported
     */
    private boolean checkNodeTypes(Set<RootNode> nodes) {
        for (RootNode node : nodes)
            try {
                node.accept(new NodeSupported());
            } catch (UnsupportedOperationException e) {
                logger.warn("Node of the type {} not supported", node.getClass().getName(), e);
                return false;
            }
        return true;
    }

    @Override
    public void prepare() {
        logger.info("Begin preparation for transformation to Cloud Foundry.");

        logger.debug("Collecting Compute Nodes in Topology");
        ComputeNodeFinder computeFinder = new ComputeNodeFinder();
        for (RootNode node : model.getNodes()) {
            node.accept(computeFinder);
            CloudFoundryNode container = new CloudFoundryNode(node);
            applicationNodes.put(node.getEntityName(), container);
        }

        for (Compute compute : computeFinder.getComputeNodes()) {
            computeNodes.add(applicationNodes.get(compute.getEntityName()));
        }

        logger.debug("Finding Top Level Nodes");
        GraphSort graph = new GraphSort(model);
        Set<RootNode> topLevelNodes = graph.getTopLevelNode(
            computeFinder.getComputeNodes().stream().map(Compute.class::cast).collect(Collectors.toList()),
            e -> applicationNodes.get(e.getEntityName()).activateParentComputeNode()
        );

        logger.debug("Building complete Topology Stacks");
        this.stacks.addAll(graph.buildStacks(topLevelNodes, applicationNodes));

        //TODO: check how many different applications there are and fill list with them
        //probably there must be a combination of application and set of nodes
        applications = new ArrayList<>();
        int i = 1;

        for (CloudFoundryStack stack : stacks) {
            Application myApp = new Application(i);
            i++;
            myApp.setProvider(provider);
            myApp.setConnection(connection);

            myApp.setName(stack.getStackName());
            myApp.addStack(stack);

            applications.add(myApp);
        }
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
        logger.info("Begin transformation to Cloud Foundry.");
        PluginFileAccess fileAccess = context.getPluginFileAccess();
        List<Application> filledApplications = new ArrayList<>();

        for (Application application : applications) {
            NodeVisitor visitor = new NodeVisitor(application);

            for (CloudFoundryNode s : application.getStack().getNodes()) {
                s.getNode().accept(visitor);
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

