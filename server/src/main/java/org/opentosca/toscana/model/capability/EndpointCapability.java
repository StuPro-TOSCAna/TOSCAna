package org.opentosca.toscana.model.capability;

import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.model.datatype.NetworkProtocol;
import org.opentosca.toscana.model.datatype.Port;
import org.opentosca.toscana.model.datatype.PortSpec;
import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.relation.ConnectsTo;
import org.opentosca.toscana.model.visitor.CapabilityVisitor;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

/**
 This is the default type that should be used or extended to define a network endpoint capability.
 This includes the information to express a basic endpoint with a single port or a complex endpoint with multiple supportedPorts.
 By default the Endpoint is assumed to represent an address on a private network unless otherwise specified.
 (TOSCA Simple Profile in YAML Version 1.1, p. 153)
 */
@Data
public class EndpointCapability extends Capability {

    /**
     The protocol that the endpoint accepts (any OSI Layer 4-7 protocols).
     (TOSCA Simple Profile in YAML Version 1.1, p. 153)
     */
    private final NetworkProtocol protocol;
    /**
     The optional port of the endpoint.
     (TOSCA Simple Profile in YAML Version 1.1, p. 153)
     */
    private final Port port;
    /**
     Requests for this endpoint to be secure and use credentials supplied on the node's {@link ConnectsTo} relationship.
     (TOSCA Simple Profile in YAML Version 1.1, p. 153)
     */
    private final boolean secure;
    /**
     The optional URL path of this endpoint’s address if applicable for the protocol.
     (TOSCA Simple Profile in YAML Version 1.1, p. 153)
     */
    private final URL urlPath;
    /**
     The optional name (or ID) of the network port this endpoint should be bound to.
     (TOSCA Simple Profile in YAML Version 1.1, p. 153)
     */
    private final String portName;
    /**
     The optional name (or ID) of the network this endpoint should be bound to.
     <p>
     Possible values: PRIVATE | PUBLIC |<network_name> | <network_id>
     (TOSCA Simple Profile in YAML Version 1.1, p. 153)
     */
    private final String networkName;
    /**
     The optional indicator of the direction of the connection.
     (TOSCA Simple Profile in YAML Version 1.1, p. 153)
     */
    private final Initiator initiator;
    /**
     The optional map of supportedPorts the Endpoint supports (if more than one)
     (TOSCA Simple Profile in YAML Version 1.1, p. 153)
     */
    private final Set<PortSpec> supportedPorts;
    /**
     The IP address as propagated up by the associated node’s host ({@link Compute}) container.
     (TOSCA Simple Profile in YAML Version 1.1, p. 153)
     */
    private final String ipAddress;

    @Builder
    protected EndpointCapability(NetworkProtocol protocol,
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
        super(validSourceTypes, occurence, description);
        if (port == null && supportedPorts.isEmpty()) {
//            (TOSCA Simple Profile in YAML Version 1.1, p. 154): Additional Requirement
            throw new IllegalArgumentException("Constraint violation: Either `port` or `supportedPorts` must provide a port");
        }
        this.protocol = Objects.nonNull(protocol) ? protocol : NetworkProtocol.TCP;
        this.port = port;
        this.secure = secure;
        this.urlPath = urlPath;
        this.portName = portName;
        this.networkName = Objects.nonNull(networkName) ? networkName : "PRIVATE";
        this.initiator = Objects.nonNull(initiator) ? initiator : Initiator.SOURCE;
        this.supportedPorts = Objects.requireNonNull(supportedPorts);
        this.ipAddress = Objects.requireNonNull(ipAddress);
    }

    /**
     @param ipAddress {@link #ipAddress}
     @param port      {@link #port}
     */
    public static EndpointCapabilityBuilder builder(String ipAddress,
                                                    Port port) {
        return new EndpointCapabilityBuilder()
            .ipAddress(ipAddress)
            .port(port);
    }

    /**
     @param ipAddress     {@link #ipAddress}
     @param supportedPort one of {@link #supportedPorts}
     */
    public static EndpointCapabilityBuilder builder(String ipAddress,
                                                    PortSpec supportedPort) {
        return new EndpointCapabilityBuilder()
            .ipAddress(ipAddress)
            .supportedPort(supportedPort);
    }

    /**
     @return {@link #port}
     */
    public Optional<Port> getPort() {
        return Optional.ofNullable(port);
    }

    /**
     @return {@link #urlPath}
     */
    public Optional<URL> getUrlPath() {
        return Optional.ofNullable(urlPath);
    }

    /**
     @return {@link #portName}
     */
    public Optional<String> getPortName() {
        return Optional.ofNullable(portName);
    }

    /**
     @return {@link #networkName}
     */
    public Optional<String> getNetworkName() {
        return Optional.ofNullable(networkName);
    }

    /**
     @return {@link #initiator}
     */
    public Optional<Initiator> getInitiator() {
        return Optional.ofNullable(initiator);
    }

    @Override
    public void accept(CapabilityVisitor v) {
        v.visit(this);
    }

    public enum Initiator {
        SOURCE,
        TARGET,
        PEER
    }

    public static class EndpointCapabilityBuilder extends CapabilityBuilder {
    }
}
