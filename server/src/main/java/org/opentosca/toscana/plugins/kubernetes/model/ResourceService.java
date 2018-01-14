package org.opentosca.toscana.plugins.kubernetes.model;

import java.util.stream.Collectors;

import org.opentosca.toscana.plugins.kubernetes.util.NodeStack;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.client.internal.SerializationUtils;

public class ResourceService {
    private final String name;
    private Pod pod;
    private Service service;

    public ResourceService(Pod pod) {
        this.pod = pod;
        this.name = pod.getName().replaceAll("_", "-");
    }

    public ResourceService build() {
        service = new ServiceBuilder()
            .withNewMetadata()
            .withName(name + "-service")
            .addToLabels("app", name + "-service")
            .endMetadata()
            .withNewSpec()
            .addAllToPorts(pod.getPorts().stream().map(Port::toServicePort).collect(Collectors.toList()))
            .addToSelector("app", name)
            .withType("NodePort")
            .endSpec()
            .build();
        return this;
    }

    public String toYaml() throws JsonProcessingException {
        return SerializationUtils.dumpAsYaml(service);
    }
}
