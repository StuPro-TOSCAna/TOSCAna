package org.opentosca.toscana.model.capability;

import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.RootNode;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

/**
 The NetworkCapability indicates that the node can provide addressability for the resource within a network.
 (TOSCA Simple Profile in YAML Version 1.1, p. 151)
 */
@Data
public class NetworkCapability extends Capability {

    /**
     The optional name (or identifier) of a specific network resource.
     (TOSCA Simple Profile in YAML Version 1.1, p. 151)
     */
    private final String name;

    @Builder
    protected NetworkCapability(String name,
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

    public static class NetworkCapabilityBuilder extends CapabilityBuilder {
    }
}
