package org.opentosca.toscana.plugins.kubernetes.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.plugins.kubernetes.docker.mapper.BaseImageMapper;
import org.opentosca.toscana.plugins.kubernetes.model.Port;
import org.opentosca.toscana.plugins.kubernetes.visitor.imgtransform.DockerfileBuildingVisitor;
import org.opentosca.toscana.plugins.kubernetes.visitor.imgtransform.ImageMappingVisitor;

import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.ServicePortBuilder;
import org.slf4j.Logger;

public class NodeStack {
    private final List<KubernetesNodeContainer> stackNodes;

    private String dockerfilePath = null;
    private String dockerImageTag = null;

    private List<Integer> openPorts = new ArrayList<>();

    public NodeStack(List<KubernetesNodeContainer> stackNodes) {
        this.stackNodes = stackNodes;
    }

    public void forEachNode(Consumer<KubernetesNodeContainer> consumer) {
        for (KubernetesNodeContainer stackNode : stackNodes) {
            consumer.accept(stackNode);
        }
    }

    public int getNodeCount() {
        return stackNodes.size();
    }

    public KubernetesNodeContainer getRootNode() {
        return stackNodes.get(stackNodes.size() - 1);
    }

    public void buildToDockerfile(TransformationContext context, BaseImageMapper mapper) throws IOException {
        Logger logger = context.getLogger(NodeStack.class);
        ImageMappingVisitor mappingVisitor = new ImageMappingVisitor(mapper);
        for (int i = stackNodes.size() - 1; i >= 0; i--) {
            KubernetesNodeContainer c = stackNodes.get(i);
            c.getNode().accept(mappingVisitor);
        }

        if (!mappingVisitor.getBaseImage().isPresent()) {
            throw new UnsupportedOperationException("Transformation of the Stack " + this + " is not possible!");
        }
        String baseImage = mappingVisitor.getBaseImage().get();
        logger.info("Determined Base Image {} for stack {}", baseImage, this);

        DockerfileBuildingVisitor visitor = new DockerfileBuildingVisitor(baseImage, this, context);
        visitor.buildAndWriteDockerfile();
        this.openPorts.addAll(visitor.getPorts());
        dockerfilePath = "output/docker/" + getStackName();
    }

    public List<ContainerPort> getOpenContainerPorts() {
        List<ContainerPort> ports = new ArrayList<>();
        openPorts.forEach(e -> ports.add(new ContainerPortBuilder().withContainerPort(e).build()));
        return Collections.unmodifiableList(ports);
    }

    public List<ServicePort> getOpenServicePorts() {
        List<ServicePort> ports = new ArrayList<>();
        openPorts.forEach(e -> ports.add(new ServicePortBuilder().withPort(e).build()));
        return Collections.unmodifiableList(ports);
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
        return stackNodes.get(0).getNode().getNodeName();
    }

    public String getCleanStackName() {
        return getStackName().replaceAll("_", "-");
    }

    public Compute getComputeNode() {
        return (Compute) this.stackNodes.stream().filter(e -> e.getNode() instanceof Compute)
            .findFirst().orElseThrow(IllegalArgumentException::new).getNode();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NodeStack (name = '").append(getStackName()).append("', topology = ");
        stackNodes.forEach(e -> {
            builder.append(e.getNode().getNodeName());
            if (!e.getNode().equals(getRootNode().getNode())) {
                builder.append(" -> ");
            }
        });
        builder.append(")");
        return builder.toString();
    }
}
