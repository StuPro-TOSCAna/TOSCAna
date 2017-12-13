package org.opentosca.toscana.plugins.kubernetes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.plugins.kubernetes.docker.image.DockerImageBuilder;
import org.opentosca.toscana.plugins.kubernetes.docker.mapper.BaseImageMapper;
import org.opentosca.toscana.plugins.kubernetes.exceptions.UnsupportedOsTypeException;
import org.opentosca.toscana.plugins.kubernetes.util.KubernetesNodeContainer;
import org.opentosca.toscana.plugins.kubernetes.util.NodeStack;
import org.opentosca.toscana.plugins.kubernetes.visitor.check.NodeTypeCheckVisitor;
import org.opentosca.toscana.plugins.kubernetes.visitor.check.OsCheckNodeVisitor;
import org.opentosca.toscana.plugins.kubernetes.visitor.util.ComputeNodeFindingVisitor;
import org.opentosca.toscana.plugins.lifecycle.AbstractLifecycle;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.extensions.DeploymentBuilder;
import io.fabric8.kubernetes.client.internal.SerializationUtils;

import static org.opentosca.toscana.plugins.kubernetes.util.GraphOperations.buildTopologyStacks;
import static org.opentosca.toscana.plugins.kubernetes.util.GraphOperations.determineTopLevelNodes;

public class KubernetesLifecycle extends AbstractLifecycle {

    private final EffectiveModel model;

    private final BaseImageMapper baseImageMapper;

    private Map<String, KubernetesNodeContainer> nodes = new HashMap<>();
    private Set<KubernetesNodeContainer> computeNodes = new HashSet<>();
    private Set<NodeStack> stacks = new HashSet<>();

    public KubernetesLifecycle(TransformationContext context, BaseImageMapper mapper) throws IOException {
        super(context);
        this.baseImageMapper = mapper;
        model = context.getModel();
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

    @Override
    public void prepare() {
        logger.debug("Collecting Compute Nodes in topology");
        ComputeNodeFindingVisitor computeFinder = new ComputeNodeFindingVisitor();
        model.getNodes().forEach(e -> {
            e.accept(computeFinder);
            KubernetesNodeContainer container = new KubernetesNodeContainer(e);
            nodes.put(e.getNodeName(), container);
        });
        computeFinder.getComputeNodes().forEach(e -> computeNodes.add(nodes.get(e.getNodeName())));

        logger.debug("Finding top Level Nodes");
        Set<RootNode> topLevelNodes = determineTopLevelNodes(
            context.getModel(),
            computeFinder.getComputeNodes().stream().map(Compute.class::cast).collect(Collectors.toList()),
            e -> nodes.get(e.getNodeName()).activateParentComputeNode()
        );

        logger.debug("Building complete Topology stacks");
        buildTopologyStacks(model, topLevelNodes, nodes);
    }

    @Override
    public void transform() {
        logger.info("Transforming...");
        createDockerfiles();
        buildDockerImages();
        createKubernetesResources();
    }

    @Override
    public void cleanup() {
        // NOOP
    }

    /**
     Builds all Dockerimages of the stack and exports the images as a tar archive.
     */
    private void buildDockerImages() {
        logger.info("Building Docker images");
        stacks.forEach(e -> {
            logger.info("Building {}", e);
            DockerImageBuilder builder = new DockerImageBuilder(e.getStackName(), "output/docker/" + e.getDockerfilePath().get(), context);
            try {
                builder.buildImage("output/docker/" + e.getStackName() + ".tar.gz");
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new RuntimeException("Transformation Failed", ex);
            }
        });
    }

    /**
     Creates the Dockerfiles (that means the dockerfile and all its dependesies get written to disk)
     */
    private void createDockerfiles() {
        stacks.forEach(e -> {
            logger.info("Creating Dockerfile for {}", e);
            try {
                e.buildToDockerfile(context, baseImageMapper);
            } catch (IOException ex) {
                ex.printStackTrace();
                throw new RuntimeException("Transformation Failed", ex);
            }
        });
    }

    /**
     Writes the kuberenetes Resources of the nodes into the output/kubernetes
     */
    private void createKubernetesResources() {
        logger.info("Creating Kubernetes Resource Descriptions");
        List<String> results = new ArrayList<>();
        stacks.forEach(e -> {
            logger.info("Creating deployment for {}", e);
            writeDeployment(results, e);
            writeService(results, e);
        });

        StringBuilder complete = new StringBuilder();
        results.forEach(e -> {
            complete.append(e);
        });

        try {
            context.getPluginFileAccess().access("output/kubernetes-resources/complete.yml").append(complete.toString()).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeService(List<String> results, NodeStack e) {
        ServiceBuilder serviceBuilder = new ServiceBuilder();
        serviceBuilder.withNewMetadata()
            .withName(e.getStackName() + "-service")
            .addToLabels("app", e.getStackName())
            .endMetadata()
            .withNewSpec()
            .addAllToPorts(e.getOpenServicePorts())
            .addToSelector("app", e.getStackName())
            .withType("NodePort")
            .endSpec();
        serializeKubernetesResource(
            results,
            serviceBuilder.build(),
            "output/kubernetes-resources/" + e.getStackName() + "-service.yml"
        );
    }

    private void writeDeployment(List<String> results, NodeStack e) {
        DeploymentBuilder deploymentBuilder = new DeploymentBuilder();
        deploymentBuilder = deploymentBuilder.withNewMetadata()
            .withName(e.getStackName())
            .endMetadata()
            .withNewSpec()
            .withReplicas(1)
            .withNewTemplate()
            .withNewMetadata()
            .addToLabels("app", e.getStackName())
            .endMetadata()
            .withNewSpec()
            .addNewContainer()
            .withName(e.getStackName())
            .withImage(e.getStackName())
            .addAllToPorts(e.getOpenContainerPorts())
            .endContainer()
            .endSpec()
            .endTemplate()
            .endSpec();
        serializeKubernetesResource(
            results,
            deploymentBuilder.build(),
            "output/kubernetes-resources/" + e.getStackName() + "-deployment.yml"
        );
    }

    private void serializeKubernetesResource(List<String> results, HasMetadata deployment, String filename) {
        try {
            String result = SerializationUtils.dumpAsYaml(deployment);
            results.add(result);
            context.getPluginFileAccess().access(filename).append(result).close();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}
