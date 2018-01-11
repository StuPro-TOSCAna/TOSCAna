package org.opentosca.toscana.plugins.kubernetes.model;

import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.ServicePortBuilder;

public class Port {
    private int port;

    public Port(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }
    
    public ServicePort toServicePort() {
        return new ServicePortBuilder().withPort(port).build();
    }
    
    public ContainerPort toContainerPort() {
        return new ContainerPortBuilder().withContainerPort(port).build();
    }
}
