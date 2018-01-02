package org.opentosca.toscana.model.capability;

import java.util.Set;

import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.visitor.CapabilityVisitor;

import lombok.Builder;
import lombok.Data;

/**
 The NodeCapability indicates the base capabilities of a TOSCA Node Type.
 (TOSCA Simple Profile in YAML Version 1.1, p. 150)
 */
@Data
public class NodeCapability extends Capability {

    @Builder
    protected NodeCapability(Set<Class<? extends RootNode>> validSourceTypes,
                             Range occurrence) {
        super(validSourceTypes, occurrence);
    }

    @Override
    public void accept(CapabilityVisitor v) {
        v.visit(this);
    }

    public static class NodeCapabilityBuilder extends CapabilityBuilder {
    }
}
