package org.opentosca.toscana.model.capability;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.visitor.CapabilityVisitor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 The NodeCapability indicates the base capabilities of a TOSCA Node Type.
 (TOSCA Simple Profile in YAML Version 1.1, p. 150)
 */
@EqualsAndHashCode
@ToString
public class NodeCapability extends Capability {

    public NodeCapability(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    @Override
    public void accept(CapabilityVisitor v) {
        v.visit(this);
    }
}
