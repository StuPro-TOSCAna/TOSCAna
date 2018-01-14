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
                                         Set<Class<? extends RootNode>> validSourceTypes,
                                         Range occurrence) {
        super(protocol, port, secure, urlPath, portName, networkName, initiator,
            supportedPorts, validSourceTypes, occurrence);
    }

    public static DatabaseEndpointCapability getFallback(DatabaseEndpointCapability endpoint) {
        return (endpoint == null) ? DatabaseEndpointCapability.builder().build() : endpoint;
    }

    @Override
    public void accept(CapabilityVisitor v) {
        v.visit(this);
    }

    public static class DatabaseEndpointCapabilityBuilder extends EndpointCapabilityBuilder {
    }
}
