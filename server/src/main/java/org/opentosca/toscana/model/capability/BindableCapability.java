package org.opentosca.toscana.model.capability;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.visitor.CapabilityVisitor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 A node that has the BindableCapability indicates that it can be bound to a logical network association via a network port.
 (TOSCA Simple Profile in YAML Version 1.1, p. 159)
 */
@EqualsAndHashCode
@ToString
public class BindableCapability extends NodeCapability {

    public BindableCapability(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    @Override
    public void accept(CapabilityVisitor v) {
        v.visit(this);
    }
}
