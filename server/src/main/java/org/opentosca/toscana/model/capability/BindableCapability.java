package org.opentosca.toscana.model.capability;

import java.util.Set;

import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.visitor.CapabilityVisitor;

import lombok.Builder;
import lombok.Data;

/**
 A node that has the BindableCapability indicates that it can be bound to a logical network association via a network port.
 (TOSCA Simple Profile in YAML Version 1.1, p. 159)
 */
@Data
public class BindableCapability extends NodeCapability {

    @Builder
    protected BindableCapability(Set<Class<? extends RootNode>> validSourceTypes,
                                 Range occurrence) {
        super(validSourceTypes, occurrence);
    }

    @Override
    public void accept(CapabilityVisitor v) {
        v.visit(this);
    }

    public static class BindableCapabilityBuilder extends NodeCapabilityBuilder {
    }
}
