package org.opentosca.toscana.plugins.kubernetes.model.transform;

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
        String name = namePrefix + "-" + port;

        //Simplify the name if its bigger than 15 Chars
        if (name.length() >= 15) {
            StringBuilder builder = new StringBuilder();
            String[] nameParts = name.split("-");
            for (String namePart : nameParts) {
                if (namePart.length() >= 1) {
                    builder.append(namePart.charAt(0));
                }
            }
            builder.append("-").append(port);
            name = builder.toString();
        }

        return name;
    }

    public ServicePort toServicePort() {
        return new ServicePortBuilder().withName(getName()).withPort(port).build();
    }

    public ContainerPort toContainerPort() {
        return new ContainerPortBuilder().withName(getName()).withContainerPort(port).build();
    }
}
