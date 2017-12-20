package org.opentosca.toscana.model.capability;

import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.visitor.CapabilityVisitor;

import lombok.Builder;
import lombok.Data;

/**
 The StorageCapability indicates that the node can provide a named storage location with specified size range.
 (TOSCA Simple Profile in YAML Version 1.1, p. 152)
 */
@Data
public class StorageCapability extends Capability {

    /**
     The optional name (or identifier) of a specific storage resource.
     (TOSCA Simple Profile in YAML Version 1.1, p. 152)
     */
    private final String name;

    @Builder
    protected StorageCapability(String name,
                                Set<Class<? extends RootNode>> validSourceTypes,
                                Range occurrence) {
        super(validSourceTypes, occurrence);
        this.name = name;
    }

    public static StorageCapability getFallback(StorageCapability c) {
        return (c == null) ? StorageCapability.builder().build() : c;
    }

    /**
     @return {@link #name}
     */
    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    @Override
    public void accept(CapabilityVisitor v) {
        v.visit(this);
    }

    public static class StorageCapabilityBuilder extends CapabilityBuilder {
    }
}
