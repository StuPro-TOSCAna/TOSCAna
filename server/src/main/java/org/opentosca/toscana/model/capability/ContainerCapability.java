package org.opentosca.toscana.model.capability;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.visitor.CapabilityVisitor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 The ContainerCapability indicates that the node can act as a container (or a host) for one or more other declared Node Types.
 */
@EqualsAndHashCode
@ToString
public class ContainerCapability extends ComputeCapability {

    public ContainerCapability(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    @Override
    public void accept(CapabilityVisitor v) {
        v.visit(this);
    }
}
