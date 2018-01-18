package org.opentosca.toscana.model.capability;

import java.net.URL;
import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.datatype.NetworkProtocol;
import org.opentosca.toscana.model.datatype.Port;
import org.opentosca.toscana.model.datatype.PortSpec;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.relation.ConnectsTo;
import org.opentosca.toscana.model.util.ToscaKey;
import org.opentosca.toscana.model.visitor.CapabilityVisitor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 This is the default type that should be used or extended to define a network endpoint capability.
 This includes the information to express a basic endpoint with a single port or a complex endpoint with multiple supportedPorts.
 By default the Endpoint is assumed to represent an address on a private network unless otherwise specified.
 (TOSCA Simple Profile in YAML Version 1.1, p. 153)
 */
@EqualsAndHashCode
@ToString
public class EndpointCapability extends Capability {

    /**
     The protocol that the endpoint accepts (any OSI Layer 4-7 protocols).
     <p>
     (TOSCA Simple Profile in YAML Version 1.1, p. 153)
     <p>
     Defaults to NetworkProtocol.TCP.
     */
    public static ToscaKey<NetworkProtocol> PROTOCOL = new ToscaKey<>(PROPERTIES, "protocol")
        .type(NetworkProtocol.class);
    /**
     The optional port of the endpoint.
     (TOSCA Simple Profile in YAML Version 1.1, p. 153)
     */
    public static ToscaKey<Port> PORT = new ToscaKey<>(PROPERTIES, "port")
        .type(Port.class);
    /**
     Requests for this endpoint to be secure and use credentials supplied on the node's {@link ConnectsTo} relationship.
     (TOSCA Simple Profile in YAML Version 1.1, p. 153)
     <p>
     Defaults to false.
     */
    public static ToscaKey<Boolean> SECURE = new ToscaKey<>(PROPERTIES, "secure")
        .type(Boolean.class);
    /**
     The optional URL path of this endpoint’s address if applicable for the protocol.
     (TOSCA Simple Profile in YAML Version 1.1, p. 153)
     */
    public static ToscaKey<URL> URL_PATH = new ToscaKey<>(PROPERTIES, "url_path")
        .type(URL.class);
    /**
     The optional name (or ID) of the network port this endpoint should be bound to.
     (TOSCA Simple Profile in YAML Version 1.1, p. 153)
     */
    public static ToscaKey<String> PORT_NAME = new ToscaKey<>(PROPERTIES, "port_name");
    /**
     The optional name (or ID) of the network this endpoint should be bound to.
     <p>
     Possible values: PRIVATE | PUBLIC |<network_name> | <network_id>
     <p>
     (TOSCA Simple Profile in YAML Version 1.1, p. 153)
     <p>
     Defaults to "PRIVATE".
     */
    public static ToscaKey<String> NETWORK_NAME = new ToscaKey<>(PROPERTIES, "network_name");
    /**
     The indicator of the direction of the connection.
     (TOSCA Simple Profile in YAML Version 1.1, p. 153)
     <p>
     Defaults to Initiator.SOURCE
     */
    public static ToscaKey<Initiator> INITIATOR = new ToscaKey<>(PROPERTIES, "initiator")
        .type(Initiator.class);
    /**
     The optional map of supportedPorts the Endpoint supports (if more than one)
     (TOSCA Simple Profile in YAML Version 1.1, p. 153)
     */
    public static ToscaKey<Set<PortSpec>> SUPPORTED_PORTS = new ToscaKey<>(PROPERTIES, "supported_ports")
        .type(PortSpec.class);
    /**
     The optional IP address as propagated up by the associated node’s host ({@link Compute}) container.
     (TOSCA Simple Profile in YAML Version 1.1, p. 153)
     */
    public static ToscaKey<String> IP_ADDRESS = new ToscaKey<>(PROPERTIES, "ip_address");

    public EndpointCapability(MappingEntity mappingEntity) {
        super(mappingEntity);
        init();
    }

    private void init() {
        setDefault(PROTOCOL, NetworkProtocol.TCP);
        setDefault(SECURE, Boolean.FALSE);
        setDefault(NETWORK_NAME, "PRIVATE");
        setDefault(INITIATOR, Initiator.SOURCE);
    }

    /**
     @return {@link #URL_PATH}
     */
    public Optional<URL> getUrlPath() {
        return Optional.ofNullable(get(URL_PATH));
    }

    /**
     Sets {@link #URL_PATH}
     */
    public EndpointCapability setUrlPath(URL urlPath) {
        set(URL_PATH, urlPath);
        return this;
    }

    /**
     @return {@link #PORT_NAME}
     */
    public Optional<String> getPortName() {
        return Optional.ofNullable(get(PORT_NAME));
    }

    /**
     Sets {@link #PORT_NAME}
     */
    public EndpointCapability setPortName(String portName) {
        set(PORT_NAME, portName);
        return this;
    }

    /**
     @return {@link #NETWORK_NAME}
     */
    public Optional<String> getNetworkName() {
        return Optional.ofNullable(get(NETWORK_NAME));
    }

    /**
     Sets {@link #NETWORK_NAME}
     */
    public EndpointCapability setNetworkName(String networkName) {
        set(NETWORK_NAME, networkName);
        return this;
    }

    /**
     @return {@link #IP_ADDRESS}
     */
    public Optional<String> getIpAddress() {
        return Optional.ofNullable(get(IP_ADDRESS));
    }

    /**
     Sets {@link #IP_ADDRESS}
     */
    public EndpointCapability setIpAddress(String ipAddress) {
        set(IP_ADDRESS, ipAddress);
        return this;
    }

    /**
     @return {@link #SUPPORTED_PORTS}
     */
    public Set<PortSpec> getSupportedPorts() {
        return get(SUPPORTED_PORTS);
    }

    /**
     Sets {@link #SUPPORTED_PORTS}
     */
    public EndpointCapability setSupportedPorts(Set<PortSpec> supportedPorts) {
        set(SUPPORTED_PORTS, supportedPorts);
        return this;
    }

    @Override
    public void accept(CapabilityVisitor v) {
        v.visit(this);
    }

    /**
     @return {@link #PORT}
     */

    public Optional<Port> getPort() {
        return Optional.ofNullable(get(PORT));
    }

    /**
     Sets {@link #PORT}
     */
    public EndpointCapability setPort(Port port) {
        set(PORT, port);
        return this;
    }

    /**
     @return {@link #PROTOCOL}
     */
    public NetworkProtocol getProtocol() {

        return get(PROTOCOL);
    }

    /**
     Sets {@link #PROTOCOL}
     */
    public EndpointCapability setProtocol(NetworkProtocol protocol) {
        set(PROTOCOL, protocol);
        return this;
    }

    /**
     @return {@link #SECURE}
     */
    public Boolean getSecure() {
        return get(SECURE);
    }

    /**
     Sets {@link #SECURE}
     */
    public EndpointCapability setSecure(Boolean secure) {
        set(SECURE, secure);
        return this;
    }

    /**
     @return {@link #INITIATOR}
     */
    public Initiator getInitiator() {
        return get(INITIATOR);
    }

    /**
     Sets {@link #INITIATOR}
     */
    public EndpointCapability setInitiator(Initiator initiator) {
        set(INITIATOR, initiator);
        return this;
    }

    public enum Initiator {
        SOURCE,
        TARGET,
        PEER
    }
}
