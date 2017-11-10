package org.opentosca.toscana.model.datatype;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 Defines a network port.
 <p>
 Originates from TOSCA's type 'PortDef' (TOSCA Simple Profile in YAML Version 1.1, p. 145)
 */
@EqualsAndHashCode
@ToString
public class Port implements Comparable<Port> {

    @Min(1)
    @Max(65535)
    public final int port;

    /**
     @param port 1 <= port <= 65535
     */
    public Port(int port) {
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException(String.format("Port '%d' is not in valid range", port));
        }
        this.port = port;
    }

    @Override
    public int compareTo(Port port) {
        return Integer.compare(this.port, port.port);
    }
}
