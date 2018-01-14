package org.opentosca.toscana.model.capability;

import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.visitor.CapabilityVisitor;

import lombok.Builder;
import lombok.Data;

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
                                Set<Class<? extends RootNode>> validSourceTypes,
                                Range occurrence) {
        super(validSourceTypes, occurrence);
        this.name = name;
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

    public static class NetworkCapabilityBuilder extends CapabilityBuilder {
    }
}
