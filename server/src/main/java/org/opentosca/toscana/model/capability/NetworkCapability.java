package org.opentosca.toscana.model.capability;

import java.util.Optional;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.util.ToscaKey;
import org.opentosca.toscana.model.visitor.CapabilityVisitor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 The NetworkCapability indicates that the node can provide addressability for the resource within a network.
 (TOSCA Simple Profile in YAML Version 1.1, p. 151)
 */
@EqualsAndHashCode
@ToString
public class NetworkCapability extends Capability {

    /**
     The optional name (or identifier) of a specific network resource.
     (TOSCA Simple Profile in YAML Version 1.1, p. 151)
     */
    public static ToscaKey<String> NAME = new ToscaKey<>(PROPERTIES, "name");

    public NetworkCapability(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    /**
     @return {@link #NAME}
     */
    public Optional<String> getName() {
        return Optional.ofNullable(get(NAME));
    }

    /**
     Sets {@link #NAME}
     */
    public NetworkCapability setName(String name) {
        set(NAME, name);
        return this;
    }

    @Override
    public void accept(CapabilityVisitor v) {
        v.visit(this);
    }
}
