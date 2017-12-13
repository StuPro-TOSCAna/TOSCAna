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
 The default TOSCA type that should be used or extended to define a specialized administrator endpoint capability.
 (TOSCA Simple Profile in YAML Version 1.1, p. 155)
 <p>
 Note: TOSCA Orchestrator handling for this type SHALL assure
 that network-level security is enforced if possible.
 (TOSCA Simple Profile in YAML Version 1.1, p. 156)
 */
@Data
public class AdminEndpointCapability extends EndpointCapability {

    @Builder
    protected AdminEndpointCapability(NetworkProtocol protocol,
                                      Port port,
                                      URL urlPath,
                                      String portName,
                                      String networkName,
                                      Initiator initiator,
                                      @Singular Set<PortSpec> ports,
                                      String ipAddress,
                                      @Singular Set<Class<? extends RootNode>> validSourceTypes,
                                      Range occurence,
                                      String description) {
        super(protocol, port, true, urlPath, portName, networkName, initiator,
            ports, ipAddress, validSourceTypes, occurence, description);
    }

    /**
     @param ipAddress {@link #ipAddress}
     */
    public static AdminEndpointCapabilityBuilder builder(String ipAddress) {
        return new AdminEndpointCapabilityBuilder().ipAddress(ipAddress);
    }

    public static class AdminEndpointCapabilityBuilder extends EndpointCapabilityBuilder {
    }

    @Override
    public void accept(CapabilityVisitor v) {
        v.visit(this);
    }
}
