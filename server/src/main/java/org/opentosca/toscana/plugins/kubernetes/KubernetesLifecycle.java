package org.opentosca.toscana.plugins.kubernetes;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.opentosca.toscana.core.plugin.lifecycle.AbstractLifecycle;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.plugins.kubernetes.docker.image.ExportingImageBuilder;
import org.opentosca.toscana.plugins.kubernetes.docker.image.ImageBuilder;
import org.opentosca.toscana.plugins.kubernetes.docker.image.PushingImageBuilder;
import org.opentosca.toscana.plugins.kubernetes.docker.mapper.BaseImageMapper;
import org.opentosca.toscana.plugins.kubernetes.docker.util.DockerRegistryCredentials;
import org.opentosca.toscana.plugins.kubernetes.exceptions.UnsupportedOsTypeException;
import org.opentosca.toscana.plugins.kubernetes.model.Pod;
import org.opentosca.toscana.plugins.kubernetes.model.RelationshipGraph;
import org.opentosca.toscana.plugins.kubernetes.util.KubernetesNodeContainer;
import org.opentosca.toscana.plugins.kubernetes.util.NodeStack;
import org.opentosca.toscana.plugins.kubernetes.util.ScriptHelper;
import org.opentosca.toscana.plugins.kubernetes.visitor.check.NodeTypeCheckVisitor;
import org.opentosca.toscana.plugins.kubernetes.visitor.check.OsCheckNodeVisitor;
import org.opentosca.toscana.plugins.kubernetes.visitor.util.ComputeNodeFindingVisitor;
import org.opentosca.toscana.plugins.util.TransformationFailureException;

import com.fasterxml.jackson.core.JsonProcessingException;

import static org.opentosca.toscana.plugins.kubernetes.util.GraphOperations.buildTopologyStacks;
import static org.opentosca.toscana.plugins.kubernetes.util.GraphOperations.determineTopLevelNodes;

public class KubernetesLifecycle extends AbstractLifecycle {

    //<editor-fold desc="Field Definition">
    private final EffectiveModel model;

    private final BaseImageMapper baseImageMapper;

    private Map<String, KubernetesNodeContainer> nodes = new HashMap<>();
    private Set<KubernetesNodeContainer> computeNodes = new HashSet<>();
    private Set<NodeStack> stacks = new HashSet<>();
    private List<Pod> pods = null;

    private boolean pushToRegistry = false;
    private Map<NodeStack, ImageBuilder> imageBuilders = new HashMap<>();
    //</editor-fold>

    public KubernetesLifecycle(TransformationContext context, BaseImageMapper mapper) throws IOException {
        super(context);
        this.baseImageMapper = mapper;
        model = context.getModel();
        //Fix failing K8s plugin test
        if (context.getInputs() == null) {
            pushToRegistry = false;
            return;
        }
        pushToRegistry = Boolean.parseBoolean(
            context.getInputs().getOrThrow(KubernetesPlugin.DOCKER_PUSH_TO_REGISTRY_PROPERTY_KEY)
        );
    }
    
    //<editor-fold desc="Checking Phase">
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
        OsCheckNodeVisitor nodeVisitor = new OsCheckNodeVisitor(logger);
        for (RootNode node : nodes) {
            try {
                node.accept(nodeVisitor);
            } catch (UnsupportedOsTypeException e) {
                logger.warn(e.getMessage(), e);
                return false;
            }
        }
        return true;
    }

    /**
     Checks if there are any unsupported node types

     @param nodes - Nodes to be checked
     @return boolean - true if successful, false otherwise
     */
    private boolean checkNodeTypes(Set<RootNode> nodes) {
        for (RootNode node : nodes)
            try {
                node.accept(new NodeTypeCheckVisitor());
            } catch (UnsupportedOperationException e) {
                logger.warn("Transformation of the type {} is not supported", node.getClass().getName(), e);
                return false;
            }
        return true;
    }
    //</editor-fold>

    //<editor-fold desc="Prepare Phase">
    @Override
    public void prepare() {
        ComputeNodeFindingVisitor computeFinder = findComputeNodes();
        Set<RootNode> topLevelNodes = findTopLevelNodes(computeFinder);
        groupStacks(topLevelNodes);
        updateAddresses();
    }

    private void groupStacks(Set<RootNode> topLevelNodes) {
        logger.debug("Building complete Topology stacks");
        this.stacks.addAll(buildTopologyStacks(model, topLevelNodes, nodes));

        logger.debug("Grouping Stacks in Pods");
        this.pods = Pod.getPods(stacks);
    }

    private void updateAddresses() {
        logger.debug("Setting Private and Public addresses of Compute Nodes");
        pods.forEach(e -> {
            e.getComputeNode().setPrivateAddress(e.getServiceName());
            e.getComputeNode().setPublicAddress(e.getServiceName());
        });
    }

    private Set<RootNode> findTopLevelNodes(ComputeNodeFindingVisitor computeFinder) {
        logger.debug("Finding top Level Nodes");
        return determineTopLevelNodes(
            context.getModel(),
            computeFinder.getComputeNodes().stream().map(Compute.class::cast).collect(Collectors.toList()),
            e -> nodes.get(e.getEntityName()).activateParentComputeNode()
        );
    }

    private ComputeNodeFindingVisitor findComputeNodes() {
        logger.debug("Collecting Compute Nodes in topology");
        ComputeNodeFindingVisitor computeFinder = new ComputeNodeFindingVisitor();
        model.getNodes().forEach(e -> {
            e.accept(computeFinder);
            KubernetesNodeContainer container = new KubernetesNodeContainer(e);
            nodes.put(e.getEntityName(), container);
        });
        computeFinder.getComputeNodes().forEach(e -> computeNodes.add(nodes.get(e.getEntityName())));
        return computeFinder;
    }
    //</editor-fold>

    //<editor-fold desc="Transform Phase">
    @Override
    public void transform() {
        logger.info("Transforming...");
        createDockerfiles();
        buildDockerImages();
        createKubernetesResources();
        writeHelperScripts();
    }

    /**
     Builds all Dockerimages of the stack and exports the images as a tar archive.
     */
    private void buildDockerImages() {
        instantiateImageBuilders();

        logger.info("Building Docker images");
        imageBuilders.forEach((stack, builder) -> {
            logger.info("Building {}", stack);
            try {
                builder.buildImage();
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new TransformationFailureException(
                    "Transformation Failed, while building a docker image for " + stack,
                    ex
                );
            }
        });

        storeDockerImages();
    }

    private void storeDockerImages() {
        logger.info("Storing Docker images");
        imageBuilders.forEach((stack, builder) -> {
            logger.info("Storing {}", stack);
            try {
                builder.storeImage();
                stack.setDockerImageTag(builder.getTag());
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new TransformationFailureException(
                    "Transformation Failed, while storing a docker image for " + stack,
                    ex
                );
            }
        });
    }

    private void instantiateImageBuilders() {
        logger.debug("Instantiating Docker Image Builders");
        stacks.stream().filter(NodeStack::stackRequiresBuilding).forEach(e -> {
            ImageBuilder builder;
            if (!pushToRegistry) {
                builder = new ExportingImageBuilder(
                    "output/docker/" + e.getStackName() + ".tar.gz",
                    e.getStackName(),
                    e.getDockerfilePath().get(),
                    context
                );
            } else {
                builder = new PushingImageBuilder(
                    DockerRegistryCredentials.fromContext(context),
                    e.getStackName(),
                    e.getDockerfilePath().get(),
                    context
                );
            }

            imageBuilders.put(e, builder);
        });
    }

    /**
     Creates the Dockerfiles (that means the dockerfile and all its dependesies get written to disk)
     */
    private void createDockerfiles() {
        RelationshipGraph connectionGraph = new RelationshipGraph(stacks);
        stacks.forEach(e -> {
            logger.info("Creating Dockerfile for {}", e);
            try {
                e.buildToDockerfile(connectionGraph, context, baseImageMapper);
            } catch (IOException ex) {
                ex.printStackTrace();
                throw new TransformationFailureException("Transformation Failed", ex);
            }
        });
    }

    /**
     Writes the kuberenetes Resources of the nodes into the output/kubernetes
     */
    private void createKubernetesResources() {
        logger.info("Creating Kubernetes Resource Descriptions");

        ResourceFileCreator creator = new ResourceFileCreator(pods);

        StringBuilder complete = new StringBuilder();
        try {
            creator.create().forEach((k, resource) -> {
                complete.append(resource);
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        try {
            context.getPluginFileAccess().access("output/kubernetes-resources/complete.yml").append(complete.toString()).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeHelperScripts() {
        logger.info("Writing Helper Scripts");
        try {
            ScriptHelper.copyScripts(pushToRegistry, context.getPluginFileAccess());
        } catch (IOException e) {
            throw new TransformationFailureException("Copying of Helper scripts failed", e);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Cleanup Phase">
    @Override
    public void cleanup() {
        removeDockerImages();
    }

    private void removeDockerImages() {
        logger.info("Removing built Docker Images");

        for (ImageBuilder builder : imageBuilders.values()) {
            try {
                builder.cleanup();
            } catch (Exception e) {
                logger.error("Docker Image Cleanup failed!", e);
                throw new TransformationFailureException(
                    "Transformaton Cleanup failed, while cleaning up Docker Images",
                    e
                );
            }
        }
    }
    //</editor-fold>
}
