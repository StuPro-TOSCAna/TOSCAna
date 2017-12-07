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
import io.fabric8.kubernetes.api.model.ReplicationController;
import io.fabric8.kubernetes.api.model.ReplicationControllerBuilder;
import io.fabric8.kubernetes.client.internal.SerializationUtils;

public class ResourceReplicationController {
    private final String name;
    private List<DockerApplication> stack;
    private ReplicationController replicationController;

    public ResourceReplicationController(String name, List<DockerApplication> stack) {
        this.stack = stack;
        this.name = name;
    }

    public ResourceReplicationController build() {
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

        replicationController = new ReplicationControllerBuilder()
            .withNewMetadata()
            .addToLabels("replication-controller", name)
            .endMetadata()
            .withNewSpec()
            .withNewTemplate()
            .withNewSpec()
            .addAllToContainers(containers)
            .endSpec()
            .endTemplate()
            .endSpec().build();
        return this;
    }

    public String toYaml() throws JsonProcessingException {
        return SerializationUtils.dumpAsYaml(replicationController);
    }
}
