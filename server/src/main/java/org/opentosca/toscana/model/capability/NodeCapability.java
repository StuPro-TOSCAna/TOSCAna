package org.opentosca.toscana.model.capability;

import java.util.Set;

import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.visitor.Visitor;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

/**
 The NodeCapability indicates the base capabilities of a TOSCA Node Type.
 (TOSCA Simple Profile in YAML Version 1.1, p. 150)
 */
@Data
public class NodeCapability extends Capability {

    @Builder
    protected NodeCapability(@Singular Set<Class<? extends RootNode>> validSourceTypes,
                             Range occurence,
                             String description) {
        super(validSourceTypes, occurence, description);
    }

    public static class NodeCapabilityBuilder extends CapabilityBuilder {
    }
    
    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
