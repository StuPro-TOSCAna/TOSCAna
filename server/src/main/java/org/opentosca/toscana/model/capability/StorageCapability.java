package org.opentosca.toscana.model.capability;

import java.util.Optional;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.util.ToscaKey;
import org.opentosca.toscana.model.visitor.CapabilityVisitor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 The StorageCapability indicates that the node can provide a named storage location with specified size range.
 (TOSCA Simple Profile in YAML Version 1.1, p. 152)
 */
@EqualsAndHashCode
@ToString
public class StorageCapability extends Capability {

    /**
     The optional name (or identifier) of a specific storage resource.
     (TOSCA Simple Profile in YAML Version 1.1, p. 152)
     */
    public static ToscaKey<String> NAME = new ToscaKey<>(PROPERTIES, "name");

    public StorageCapability(MappingEntity mappingEntity) {
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
    public StorageCapability setName(String name) {
        set(NAME, name);
        return this;
    }

    @Override
    public void accept(CapabilityVisitor v) {
        v.visit(this);
    }
}

