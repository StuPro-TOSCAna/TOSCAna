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

/**
 Converts a {@link Pod} object into a Kubernetes Deployment
 */
public class ResourceDeployment implements IKubernetesResource<ResourceDeployment> {
    private final String name;
    private Pod pod;
    private Deployment deployment;

    public ResourceDeployment(Pod stack) {
        this.pod = stack;
        this.name = stack.getName();
    }

    @Override
    public ResourceDeployment build() {
        ArrayList<Container> containers = new ArrayList<>();

        // Create the Container objects of the containers belonging in the Pod
        pod.getContainers().forEach(e -> {
            Container container = new ContainerBuilder()
                // Set the Image Tag
                .withImage(e.getDockerImageTag().get())
                // Set the Container Name
                .withName(e.getCleanStackName())
                // This forces the Kubernetes Cluster to check for newer Versions of the image
                .withImagePullPolicy("Always")
                // Add the Ports exposed by the Container
                .addAllToPorts(e.getOpenPorts().stream().map(Port::toContainerPort).collect(Collectors.toList()))
                .build();

            containers.add(container);
        });

        deployment = new DeploymentBuilder()
            .withNewMetadata()
            .withName(pod.getDeploymentName())
            // The Unspecific name is used here to improve the ability to find objects belonging together
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

    @Override
    public String toYaml() throws JsonProcessingException {
        return SerializationUtils.dumpAsYaml(deployment);
    }

    @Override
    public String getName() {
        return pod.getDeploymentName();
    }
}
