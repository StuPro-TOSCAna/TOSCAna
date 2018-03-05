package org.opentosca.toscana.plugins.kubernetes.model.kuberesource;

import java.util.ArrayList;
import java.util.stream.Collectors;

import org.opentosca.toscana.plugins.kubernetes.model.transform.Pod;
import org.opentosca.toscana.plugins.kubernetes.model.transform.Port;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerBuilder;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import io.fabric8.kubernetes.api.model.extensions.DeploymentBuilder;
import io.fabric8.kubernetes.client.internal.SerializationUtils;

public class ResourceDeployment {
    private final String name;
    private Pod pod;
    private Deployment deployment;

    public ResourceDeployment(Pod stack) {
        this.pod = stack;
        this.name = stack.getName();
    }

    public ResourceDeployment build() {
        ArrayList<Container> containers = new ArrayList<>();

        pod.getContainers().forEach(e -> {
            Container container = new ContainerBuilder()
                .withImage(e.getDockerImageTag().get())
                .withName(e.getCleanStackName())
                .withImagePullPolicy("Always") //TODO Consider removing this (only needed during development)
                .addAllToPorts(e.getOpenPorts().stream().map(Port::toContainerPort).collect(Collectors.toList()))
                .build();

            containers.add(container);
        });

        deployment = new DeploymentBuilder()
            .withNewMetadata()
            .withName(name + "-deployment")
            .addToLabels("app", name)
            .endMetadata()
            .withNewSpec()
            .withReplicas(pod.getReplicaCount())
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
