package org.opentosca.toscana.model.capability;

import java.net.URL;
import java.util.Set;

import org.opentosca.toscana.model.datatype.NetworkProtocol;
import org.opentosca.toscana.model.datatype.Port;
import org.opentosca.toscana.model.datatype.PortSpec;
import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.visitor.CapabilityVisitor;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

/**
 The default TOSCA type that should be used or extended to define a specialized database endpoint capability.
 (TOSCA Simple Profile in YAML Version 1.1, p. 156)
 */
@Data
public class DatabaseEndpointCapability extends EndpointCapability {

    @Builder
    protected DatabaseEndpointCapability(NetworkProtocol protocol,
                                         Port port,
                                         boolean secure,
                                         URL urlPath,
                                         String portName,
                                         String networkName,
                                         Initiator initiator,
                                         @Singular Set<PortSpec> supportedPorts,
                                         String ipAddress,
                                         Set<Class<? extends RootNode>> validSourceTypes,
                                         Range occurence,
                                         String description) {
        super(protocol, port, secure, urlPath, portName, networkName, initiator,
            supportedPorts, ipAddress, validSourceTypes, occurence, description);
    }

    /**
     @param ipAddress {@link #ipAddress}
     @param port      {@link #port}
     */
    public static DatabaseEndpointCapabilityBuilder builder(String ipAddress,
                                                            Port port) {
        return new DatabaseEndpointCapabilityBuilder()
            .ipAddress(ipAddress)
            .port(port);
    }

    /**
     @param ipAddress     {@link #ipAddress}
     @param supportedPort {@link #supportedPorts}
     */
    public static DatabaseEndpointCapabilityBuilder builder(String ipAddress,
                                                            PortSpec supportedPort) {
        return new DatabaseEndpointCapabilityBuilder()
            .ipAddress(ipAddress)
            .supportedPort(supportedPort);
    }

    @Override
    public void accept(CapabilityVisitor v) {
        v.visit(this);
    }

    public static class DatabaseEndpointCapabilityBuilder extends EndpointCapabilityBuilder {
    }
}
