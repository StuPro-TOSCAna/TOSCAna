package org.opentosca.toscana.plugins.kubernetes.model.kuberesource;

import java.util.List;
import java.util.stream.Collectors;

import org.opentosca.toscana.plugins.kubernetes.model.transform.Pod;
import org.opentosca.toscana.plugins.kubernetes.model.transform.Port;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.client.internal.SerializationUtils;

/**
 Converts a Pod (in our own model) to a
 */
public class ResourceService implements IKubernetesResource<ResourceService> {
    private final String name;
    private Pod pod;
    private Service service;

    public ResourceService(Pod pod) {
        this.pod = pod;
        this.name = pod.getName().replaceAll("_", "-");
    }

    @Override
    public ResourceService build() {
        List<ServicePort> ports = pod.getPorts().stream().map(Port::toServicePort).collect(Collectors.toList());
        service = new ServiceBuilder()
            .withNewMetadata()
            .withName(pod.getServiceName())
            .addToLabels("app", pod.getServiceName())
            .endMetadata()
            .withNewSpec()
            .addAllToPorts(ports)
            .addToSelector("app", name)
            // Use NodePort As the default Type (Many other types require more user input to work properly)
            // With the ability to access them from outside of the cluster
            .withType("NodePort")
            .endSpec()
            .build();
        return this;
    }

    @Override
    public String toYaml() throws JsonProcessingException {
        return SerializationUtils.dumpAsYaml(service);
    }

    @Override
    public String getName() {
        return pod.getServiceName();
    }
}
