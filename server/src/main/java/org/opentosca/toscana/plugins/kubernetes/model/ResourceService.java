package org.opentosca.toscana.plugins.kubernetes.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.client.internal.SerializationUtils;

public class ResourceService {
    private String name;
    private String selector;
    private Service service;

    public ResourceService(String name, String selector) {
        this.name = name;
        this.selector = selector;
    }

    public ResourceService build() {
        service = new ServiceBuilder()
            .withNewMetadata()
            .withName(name)
            .addToLabels("app", name)
            .endMetadata()
            .withNewSpec()
            .addNewPort()
            .withPort(80)
            .endPort()
            .addToSelector("app", selector)
            .withType("NodePort")
            .endSpec()
            .build();
        return this;
    }

    public String toYaml() throws JsonProcessingException {
        return SerializationUtils.dumpAsYaml(service);
    }
}
