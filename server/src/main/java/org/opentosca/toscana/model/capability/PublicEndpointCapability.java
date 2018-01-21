package org.opentosca.toscana.model.capability;

import java.util.Optional;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.util.ToscaKey;
import org.opentosca.toscana.model.visitor.CapabilityVisitor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 Represents a public endpoint which is accessible to the general internet (and its public IP
 address ranges).
 <p>
 This public endpoint capability also can be used to create a floating (IP) address that the underlying
 network assigns from a pool allocated from the application’s underlying public network. This floating
 address is managed by the underlying network such that can be routed an application’s private address
 and remains reliable to internet clients.
 (TOSCA Simple Profile in YAML Version 1.1, p. 154)
 <p>
 Note: If the networkName set to the name of a network (or sub-network) that is not public
 (i.e., has non-public IP address ranges assigned to it), then TOSCA orchestrators SHALL treat this as an error.
 (TOSCA Simple Profile in YAML Version 1.1, p. 155)
 */
@EqualsAndHashCode
@ToString
public class PublicEndpointCapability extends EndpointCapability {

    /**
     The optional name to register with DNS (experimental)
     <p>
     Note: If a dns_name is set, TOSCA orchestrators SHALL attempt to register the name
     in the (local) DNS registry for the Cloud Provider.
     <p>
     (TOSCA Simple Profile in YAML Version 1.1, p. 155)
     */
    public static final ToscaKey<String> DNS_NAME = new ToscaKey<>(PROPERTIES, "dnsName");
    /**
     indicates that the public address should be allocated from a pool of floating IPs
     that are associated with the network.
     (TOSCA Simple Profile in YAML Version 1.1, p. 155)
     <p>
     Defaults to false.
     */
    public static ToscaKey<Boolean> FLOATING = new ToscaKey<>(PROPERTIES, "floating");

    public PublicEndpointCapability(MappingEntity mappingEntity) {
        super(mappingEntity);
        init();
    }

    private void init() {
        setDefault(FLOATING, false);
    }

    /**
     @return {@link #DNS_NAME}
     */
    public Optional<String> getDnsName() {
        return Optional.ofNullable(get(DNS_NAME));
    }

    /**
     @return {@link #FLOATING}
     */
    public Boolean getFloating() {
        return get(FLOATING);
    }

    /**
     Sets {@link #FLOATING}
     */
    public PublicEndpointCapability setFloating(Boolean floating) {
        set(FLOATING, floating);
        return this;
    }

    @Override
    public void accept(CapabilityVisitor v) {
        v.visit(this);
    }
}
