package org.opentosca.toscana.model.capability;

import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.RootNode;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

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
                                @Singular Set<Class<? extends RootNode>> validSourceTypes,
                                Range occurence,
                                String description) {
        super(validSourceTypes, occurence, description);
        this.name = name;
    }

    /**
     @return {@link #name}
     */
    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public static class StorageCapabilityBuilder extends CapabilityBuilder {
    }
}
