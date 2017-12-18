package org.opentosca.toscana.plugins.kubernetes.model;

import org.opentosca.toscana.plugins.kubernetes.util.NodeStack;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.client.internal.SerializationUtils;

public class ResourceService {
    private NodeStack stack;
    private Service service;

    public ResourceService(NodeStack stack) {
        this.stack = stack;
    }

    public ResourceService build() {
        service = new ServiceBuilder()
            .withNewMetadata()
            .withName(stack.getStackName() + "-service")
            .addToLabels("app", stack.getStackName() + "-service")
            .endMetadata()
            .withNewSpec()
            .addAllToPorts(stack.getOpenServicePorts())
            .addToSelector("app", stack.getStackName())
            .withType("NodePort")
            .endSpec()
            .build();
        return this;
    }

    public String toYaml() throws JsonProcessingException {
        return SerializationUtils.dumpAsYaml(service);
    }
}
