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
import org.opentosca.toscana.plugins.kubernetes.model.transform.Port;
import org.opentosca.toscana.plugins.kubernetes.model.transform.RelationshipGraph;
import org.opentosca.toscana.plugins.kubernetes.visitor.imgtransform.DockerfileBuildingVisitor;
import org.opentosca.toscana.plugins.kubernetes.visitor.imgtransform.ImageMappingVisitor;

import org.slf4j.Logger;

public class NodeStack {
    private final List<KubernetesNodeContainer> stackNodes;

    private String dockerfilePath = null;
    private String dockerImageTag = null;

    private boolean requiresBuilding = true;

    private List<Integer> openPorts = new ArrayList<>();

    public NodeStack(List<KubernetesNodeContainer> stackNodes) {
        this.stackNodes = stackNodes;
    }

    public void forEachNode(Consumer<KubernetesNodeContainer> consumer) {
        for (KubernetesNodeContainer stackNode : stackNodes) {
            consumer.accept(stackNode);
        }
    }

    public boolean hasNode(String name) {
        return stackNodes.stream().anyMatch(e -> e.getNode().getEntityName().equals(name));
    }

    public int getNodeCount() {
        return stackNodes.size();
    }

    public List<KubernetesNodeContainer> getNodes() {
        return stackNodes;
    }

    public KubernetesNodeContainer getRootNode() {
        return stackNodes.get(stackNodes.size() - 1);
    }

    public void buildToDockerfile(
        RelationshipGraph connectionGraph,
        TransformationContext context,
        BaseImageMapper mapper
    ) throws IOException {
        Logger logger = context.getLogger(NodeStack.class);
        ImageMappingVisitor mappingVisitor = mapToBaseImage(mapper);
        String baseImage = mappingVisitor.getBaseImage().get();
        logger.info("Determined Base Image {} for stack {}", baseImage, this);

        if (!mappingVisitor.containerRequiresBuilding()) {
            this.dockerImageTag = baseImage;
            this.requiresBuilding = false;

            //TODO (IMPORTANT) this is only Proof of concept (add port detection later)
            this.openPorts.add(80);

            return;
        }

        DockerfileBuildingVisitor visitor =
            new DockerfileBuildingVisitor(baseImage, this, connectionGraph, context);
        visitor.buildAndWriteDockerfile();
        this.openPorts.addAll(visitor.getPorts());
        dockerfilePath = "output/docker/" + getStackName();
    }

    private ImageMappingVisitor mapToBaseImage(BaseImageMapper mapper) {
        ImageMappingVisitor mappingVisitor = new ImageMappingVisitor(mapper);
        for (int i = stackNodes.size() - 1; i >= 0; i--) {
            KubernetesNodeContainer c = stackNodes.get(i);
            c.getNode().accept(mappingVisitor);
        }

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
