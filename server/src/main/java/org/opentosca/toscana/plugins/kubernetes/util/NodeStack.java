package org.opentosca.toscana.plugins.kubernetes.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.plugins.kubernetes.docker.mapper.BaseImageMapper;
import org.opentosca.toscana.plugins.kubernetes.model.transform.ConnectionGraph;
import org.opentosca.toscana.plugins.kubernetes.model.transform.Port;
import org.opentosca.toscana.plugins.kubernetes.visitor.imgtransform.DockerfileBuildingVisitor;
import org.opentosca.toscana.plugins.kubernetes.visitor.imgtransform.ImageMappingVisitor;

import org.slf4j.Logger;

/**
 Represents a list of nodes in a topology that will land in a Docker Image
 */
public class NodeStack {
    /**
     The list of tosca nodes in the node stack
     <p>
     Wrapped in a {@link KubernetesNodeContainer}
     */
    private final List<KubernetesNodeContainer> stackNodes;

    /**
     The path to the created Dockerfile
     <p>
     This value is null if the Dockerfile has not been generated
     */
    private String dockerfilePath = null;
    /**
     The tag of the resulting container image
     This only gets set, if the NodeStack is based on a DockerApplication. This variable in that case refers to the tag
     of the image of the DockerApplication
     <p>
     Unless this value has been set, this is null
     */
    private String dockerImageTag = null;

    /**
     This gets set to false if the NodeStack does not require building (a Dockerfile does not have to be built)
     This is the case if the NodeStack is based on a DockerApplication
     */
    private boolean requiresBuilding = true;

    /**
     Contains all open ports, after the Dockerfile creation process is done
     this also includes DockerApplication based node stacks, after we know that the stack is
     DockerApplication based we can add the Pods.
     <p>
     The list is empty if the creation process has not been invoked or if the Node Stack does not expose any ports
     */
    private List<Integer> openPorts = new ArrayList<>();

    public NodeStack(List<KubernetesNodeContainer> stackNodes) {
        this.stackNodes = stackNodes;
    }

    /**
     Performs a for each loop over each node in the Node Stack

     @param consumer the operation to perform for each node in the node stack
     */
    public void forEachNode(Consumer<KubernetesNodeContainer> consumer) {
        for (KubernetesNodeContainer stackNode : stackNodes) {
            consumer.accept(stackNode);
        }
    }

    /**
     @return Returns true if the node stack contains a node with the given name (case sensitive)
     */
    public boolean hasNode(String name) {
        return stackNodes.stream().anyMatch(e -> e.getNode().getEntityName().equals(name));
    }

    public List<KubernetesNodeContainer> getNodes() {
        return stackNodes;
    }

    /**
     @return Returns the lowest node in the stack (usualy this is the Compute node)
     */
    public KubernetesNodeContainer getRootNode() {
        return stackNodes.get(stackNodes.size() - 1);
    }

    /**
     Creates the Dockerfile for the node stack
     <p>
     This operation also determines if the node stack is DockerApplication based, in this case
     a Dockerfile will not be created.
     */
    public void buildToDockerfile(
        ConnectionGraph connectionGraph,
        TransformationContext context,
        BaseImageMapper mapper
    ) throws IOException {
        Logger logger = context.getLogger(NodeStack.class);
        // Determine Base Image
        ImageMappingVisitor mappingVisitor = mapToBaseImage(mapper);
        String baseImage = mappingVisitor.getBaseImage().get();
        logger.info("Determined Base Image {} for stack {}", baseImage, this);

        // If the stack is Docker Application based
        // Set the resulting image and add the exposed 
        // Ports
        if (!mappingVisitor.containerRequiresBuilding()) {
            this.dockerImageTag = baseImage;
            this.requiresBuilding = false;

            //TODO (IMPORTANT) this is only Proof of concept (add port detection later)
            this.openPorts.add(80);

            return;
        }

        // Build the Dockerfile
        DockerfileBuildingVisitor visitor =
            new DockerfileBuildingVisitor(baseImage, this, connectionGraph, context);
        visitor.buildAndWriteDockerfile();
        this.openPorts.addAll(visitor.getPorts());
        dockerfilePath = "output/docker/" + getStackName();
    }

    private ImageMappingVisitor mapToBaseImage(BaseImageMapper mapper) {
        // Find the Base Image Using the ImageMappingVisitor
        ImageMappingVisitor mappingVisitor = new ImageMappingVisitor(mapper);
        for (int i = stackNodes.size() - 1; i >= 0; i--) {
            KubernetesNodeContainer c = stackNodes.get(i);
            c.getNode().accept(mappingVisitor);
        }

        // Fail if a base image could not get determined
        if (!mappingVisitor.getBaseImage().isPresent()) {
            throw new UnsupportedOperationException("Transformation of the Stack " + this + " is not possible!");
        }
        return mappingVisitor;
    }

    public List<Port> getOpenPorts() {
        return openPorts.stream().map(e -> new Port(e, this.getCleanStackName())).collect(Collectors.toList());
    }

    public Optional<String> getDockerfilePath() {
        return Optional.ofNullable(dockerfilePath);
    }

    public void setDockerImageTag(String dockerImageTag) {
        this.dockerImageTag = dockerImageTag;
    }

    public Optional<String> getDockerImageTag() {
        return Optional.ofNullable(dockerImageTag);
    }

    public String getStackName() {
        return stackNodes.get(0).getNode().getEntityName();
    }

    /**
     Returns the name of the node stack (underscores get replaced with dashes)
     */
    public String getCleanStackName() {
        return getStackName().replaceAll("_", "-");
    }

    public Compute getComputeNode() {
        return (Compute) this.stackNodes.stream().filter(e -> e.getNode() instanceof Compute)
            .findFirst().orElseThrow(IllegalArgumentException::new).getNode();
    }

    public boolean hasNode(RootNode node) {
        return stackNodes.stream().filter(e -> e.getNode().equals(node)).count() == 1;
    }

    public boolean stackRequiresBuilding() {
        return requiresBuilding;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NodeStack (name = '").append(getStackName()).append("', topology = ");
        stackNodes.forEach(e -> {
            builder.append(e.getNode().getEntityName());
            if (!e.getNode().equals(getRootNode().getNode())) {
                builder.append(" -> ");
            }
        });
        builder.append(")");
        return builder.toString();
    }
}
