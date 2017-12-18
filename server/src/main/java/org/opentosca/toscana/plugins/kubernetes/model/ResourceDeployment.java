package org.opentosca.toscana.plugins.kubernetes.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.opentosca.toscana.model.artifact.Artifact;
import org.opentosca.toscana.model.node.DockerApplication;
import org.opentosca.toscana.model.operation.Operation;
import org.opentosca.toscana.plugins.kubernetes.util.NodeStack;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerBuilder;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import io.fabric8.kubernetes.api.model.extensions.DeploymentBuilder;
import io.fabric8.kubernetes.client.internal.SerializationUtils;

public class ResourceDeployment {
    private final String name;
    private NodeStack stack;
    private Deployment deployment;

    public ResourceDeployment(NodeStack stack) {
        this.stack = stack;
        this.name = stack.getStackName();
    }

    public ResourceDeployment build() {
        ArrayList<Container> containers = new ArrayList<>();

        Container container = new ContainerBuilder()
            .withName(name)
            .withImage(stack.getStackName())
            .addAllToPorts(stack.getOpenContainerPorts()).build();
        containers.add(container);

        deployment = new DeploymentBuilder()
            .withNewMetadata()
            .withName(name + "-deployment")
            .addToLabels("app", name)
            .endMetadata()
            .withNewSpec()
            .withNewSelector()
            .addToMatchLabels("app", name)
            .endSelector()
            .withNewTemplate()
            .withNewMetadata()
            .withName(name)
            .addToLabels("app", name)
            .endMetadata()
            .withNewSpec()
            .addAllToContainers(containers)
            .endSpec()
            .endTemplate()
            .endSpec().build();
        return this;
    }

    public String toYaml() throws JsonProcessingException {
        return SerializationUtils.dumpAsYaml(deployment);
    }
}
