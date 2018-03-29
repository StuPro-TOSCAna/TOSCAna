package org.opentosca.toscana.plugins.kubernetes.model.transform;

import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.ServicePortBuilder;

/**
 Represents a Exposed Port of a Pod
 */
public class Port {
    private int port;
    private String namePrefix;

    /**
     Construct a new Port

     @param port       the Port, value should be between 0 and 65.536 (This is not getting checked though!)
     @param namePrefix a Prefix string (should be as unique as possible)
     that gets prefixed to the Port number when retrieving the name of the port
     */
    public Port(int port, String namePrefix) {
        this.port = port;
        this.namePrefix = namePrefix;
    }

    public int getPort() {
        return port;
    }

    /**
     Returns the (Kubernetes conforming name of the Port)
     <p>
     This means the Original name (<code>namePrefix + "-" + port</code>) gets shrunk if the resulting string is longer than
     15 characters (Kubernetes limits the Port Name length to that)
     */
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

    /**
     @return Converts the Port object to a Kubernetes Service Port
     */
    public ServicePort toServicePort() {
        return new ServicePortBuilder().withName(getName()).withPort(port).build();
    }

    /**
     @return Converts the Port object to a Kubernetes Container Port
     */
    public ContainerPort toContainerPort() {
        return new ContainerPortBuilder().withName(getName()).withContainerPort(port).build();
    }
}
