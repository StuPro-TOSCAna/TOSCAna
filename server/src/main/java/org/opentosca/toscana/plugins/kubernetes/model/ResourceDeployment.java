package org.opentosca.toscana.plugins.kubernetes.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.opentosca.toscana.model.artifact.Artifact;
import org.opentosca.toscana.model.node.DockerApplication;
import org.opentosca.toscana.model.operation.Operation;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerBuilder;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import io.fabric8.kubernetes.api.model.extensions.DeploymentBuilder;
import io.fabric8.kubernetes.client.internal.SerializationUtils;

public class ResourceDeployment {
    private final String name;
    private List<DockerApplication> stack;
    private Deployment deployment;

    public ResourceDeployment(String name, List<DockerApplication> stack) {
        this.stack = stack;
        this.name = name;
    }

    public ResourceDeployment build() {
        ArrayList<Container> containers = new ArrayList<>();
        for (DockerApplication app : stack) {
            String imagePath = "";
            Optional<Operation> createOperation = app.getStandardLifecycle().getCreate();
            if (createOperation.isPresent()) {
                Optional<Artifact> artifactOptional = createOperation.get().getArtifact();
                if (artifactOptional.isPresent()) {
                    Artifact artifact = artifactOptional.get();
                    // TODO use repository, currently defaults to docker hub
                    imagePath = artifact.getFilePath();
                    System.out.println(imagePath);
                }
            }
            System.out.println(app.getNodeName());
            Container container = new ContainerBuilder()
                .withName(app.getNodeName().toLowerCase())
                .withImage(imagePath)
                .addNewPort()
                .withContainerPort(80)
                .endPort().build();
            containers.add(container);
        }

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
