package org.opentosca.toscana.plugins.kubernetes.model;

import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.ServicePortBuilder;

public class Port {
    private int port;
    private String namePrefix;

    public Port(int port, String namePrefix) {
        this.port = port;
        this.namePrefix = namePrefix;
    }

    public int getPort() {
        return port;
    }

    public String getName() {
        return namePrefix + "-" + port;
    }

    public ServicePort toServicePort() {
        return new ServicePortBuilder().withName(getName()).withPort(port).build();
    }

    public ContainerPort toContainerPort() {
        return new ContainerPortBuilder().withName(getName()).withContainerPort(port).build();
    }
}
