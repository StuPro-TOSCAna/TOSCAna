package org.opentosca.toscana.plugins.cloudfoundry;

import java.io.FileNotFoundException;
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
import org.opentosca.toscana.core.transformation.properties.NoSuchPropertyException;
import org.opentosca.toscana.core.transformation.properties.PropertyInstance;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.relation.RootRelationship;
import org.opentosca.toscana.plugins.cloudfoundry.application.Application;
import org.opentosca.toscana.plugins.cloudfoundry.application.Provider;
import org.opentosca.toscana.plugins.cloudfoundry.client.Connection;
import org.opentosca.toscana.plugins.cloudfoundry.filecreator.FileCreator;
import org.opentosca.toscana.plugins.cloudfoundry.visitor.NodeTypeCheck;
import org.opentosca.toscana.plugins.cloudfoundry.visitor.NodeVisitor;
import org.opentosca.toscana.plugins.cloudfoundry.visitor.OSCheck;
import org.opentosca.toscana.plugins.cloudfoundry.visitor.PrepareVisitor;
import org.opentosca.toscana.plugins.kubernetes.exceptions.UnsupportedOsTypeException;
import org.opentosca.toscana.plugins.kubernetes.util.KubernetesNodeContainer;
import org.opentosca.toscana.plugins.kubernetes.util.NodeStack;
import org.opentosca.toscana.plugins.kubernetes.visitor.util.ComputeNodeFindingVisitor;
import org.opentosca.toscana.plugins.util.TransformationFailureException;

import org.jgrapht.Graph;
import org.json.JSONException;

import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryPlugin.CF_PROPERTY_KEY_API;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryPlugin.CF_PROPERTY_KEY_ORGANIZATION;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryPlugin.CF_PROPERTY_KEY_PASSWORD;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryPlugin.CF_PROPERTY_KEY_SPACE;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryPlugin.CF_PROPERTY_KEY_USERNAME;
import static org.opentosca.toscana.plugins.kubernetes.util.GraphOperations.buildTopologyStacks;
import static org.opentosca.toscana.plugins.kubernetes.util.GraphOperations.determineTopLevelNodes;

public class CloudFoundryLifecycle extends AbstractLifecycle {

    private final EffectiveModel model;
    private Provider provider;
    private Connection connection;
    private List<Application> applications;
    private Map<String, KubernetesNodeContainer> nodes = new HashMap<>();
    private Set<KubernetesNodeContainer> computeNodes = new HashSet<>();
    private Set<NodeStack> stacks = new HashSet<>();
    private Map<RootNode, Application> nodeApplicationMap = new HashMap<>();
    private Graph<RootNode, RootRelationship> graph;
    private List<Application> filledApplications;

    public CloudFoundryLifecycle(TransformationContext context) throws IOException, NoSuchPropertyException {
        super(context);
        model = context.getModel();

        PropertyInstance properties = context.getInputs();

        logger.debug("Checking for Properties");

        if (!properties.isEmpty()) {
            String username = properties.getOrThrow(CF_PROPERTY_KEY_USERNAME);
            String password = properties.getOrThrow(CF_PROPERTY_KEY_PASSWORD);
            String organization = properties.getOrThrow(CF_PROPERTY_KEY_ORGANIZATION);
            String space = properties.getOrThrow(CF_PROPERTY_KEY_SPACE);
            String apiHost = properties.getOrThrow(CF_PROPERTY_KEY_API);

            if (isNotNull(username, password, organization, space, apiHost)) {

                connection = new Connection(username, password,
                    apiHost, organization, space, context);

                //TODO: check how to get used provider or figure out whether it is necessary to know it?
                provider = new Provider(Provider.CloudFoundryProviderType.PIVOTAL);
                provider.setOfferedService(connection.getServices());
            }
        }
    }

    @Override
    public boolean checkModel() {
        Set<RootNode> nodes = model.getNodes();
        boolean nodeTypeCheck = checkNodeTypes(nodes);
        boolean osTypeCheck = checkOsType(nodes);
        return nodeTypeCheck && osTypeCheck;
    }

    /**
     Checks if the model contains a unsupported os

     @param nodes - Nodes to be checked
     @return boolean - true if successful, false otherwise
     */
    private boolean checkOsType(Set<RootNode> nodes) {
        OSCheck osCheck = new OSCheck(logger);
        try {
            nodes.forEach(node -> node.accept(osCheck));
        } catch (UnsupportedOsTypeException e) {
            logger.error("Check OS Type finished with failure");
            return false;
        }
        return true;
    }

    /**
     Checks if there are any unsupported node types

     @param nodes - Nodes to be checked
     @return boolean - true if successful, false otherwise
     */
    private boolean checkNodeTypes(Set<RootNode> nodes) {
        NodeTypeCheck nodeTypeCheck = new NodeTypeCheck();
        for (RootNode node : nodes)
            try {
                node.accept(nodeTypeCheck);
            } catch (UnsupportedOperationException e) {
                logger.warn("Transformation of the type {} is not supported yet by the Cloud Foundry plugin"
                    , node.getClass().getName(), e);
                return false;
            }
        return true;
    }

    @Override
    public void prepare() {

        logger.info("Begin preparation for transformation to Cloud Foundry");

        PrepareVisitor prepareVisitor = new PrepareVisitor(logger);
        for (RootNode node : context.getModel().getNodes()) {
            node.accept(prepareVisitor);
        }

        logger.debug("Collecting Compute Nodes in topology");
        ComputeNodeFindingVisitor computeFinder = new ComputeNodeFindingVisitor();
        model.getNodes().forEach(e -> {
            e.accept(computeFinder);
            KubernetesNodeContainer container = new KubernetesNodeContainer(e);
            nodes.put(e.getEntityName(), container);
        });
        computeFinder.getComputeNodes().forEach(e -> computeNodes.add(nodes.get(e.getEntityName())));

        logger.debug("Finding top Level Nodes");
        graph = model.getTopology();
        Set<RootNode> topLevelNodes = determineTopLevelNodes(
            context.getModel(),
            computeFinder.getComputeNodes().stream().map(Compute.class::cast).collect(Collectors.toList()),
            e -> nodes.get(e.getEntityName()).activateParentComputeNode()
        );

        logger.debug("Building complete Topology stacks");
        this.stacks.addAll(buildTopologyStacks(model, topLevelNodes, nodes));

        //TODO: check how many different applications there are and fill list with them
        //probably there must be a combination of application and set of nodes
        applications = new ArrayList<>();
        int i = 1;

        for (NodeStack stack : stacks) {
            Application myApp = new Application(i, context);
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

        fillApplications();

        try {
            FileCreator fileCreator = new FileCreator(fileAccess, filledApplications, context);
            fileCreator.createFiles();
        } catch (FileNotFoundException f) {
            throw new TransformationFailureException("A file which is declared in the template could not be found", f);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cleanup() {
        //throw new UnsupportedOperationException();
    }

    /**
     Fills the Applications with the sorted Node structure
     */
    public void fillApplications() {
        filledApplications = new ArrayList<>();

        for (Application app : applications) {
            for (int i = 0; i < app.getStack().getNodes().size(); i++) {
                nodeApplicationMap.put(app.getStack().getNodes().get(i).getNode(), app);
            }
        }

        for (Application application : applications) {
            NodeVisitor visitor = new NodeVisitor(application, nodeApplicationMap, graph, logger);

            for (KubernetesNodeContainer s : application.getStack().getNodes()) {
                s.getNode().accept(visitor);
            }

            Application filledApplication = visitor.getFilledApp();
            filledApplications.add(filledApplication);
        }
    }

    public List<Application> getFilledApplications() {
        return filledApplications;
    }
}   

