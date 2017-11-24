package org.opentosca.toscana.model.capability;

import java.net.URL;
import java.util.Set;

import org.opentosca.toscana.model.datatype.NetworkProtocol;
import org.opentosca.toscana.model.datatype.Port;
import org.opentosca.toscana.model.datatype.PortSpec;
import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.RootNode;

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
                                         @Singular Set<PortSpec> ports,
                                         String ipAddress,
                                         @Singular Set<Class<? extends RootNode>> validSourceTypes,
                                         Range occurence,
                                         String description) {
        super(protocol, port, secure, urlPath, portName, networkName, initiator, ports, ipAddress, validSourceTypes, occurence, description);
    }

    /**
     @param ipAddress {@link #ipAddress}
     */
    public static DatabaseEndpointCapabilityBuilder builder(String ipAddress) {
        return new DatabaseEndpointCapabilityBuilder().ipAddress(ipAddress);
    }

    public static class DatabaseEndpointCapabilityBuilder extends EndpointCapabilityBuilder {
    }
}
