package org.opentosca.toscana.model.capability;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.node.BlockStorage;
import org.opentosca.toscana.model.visitor.CapabilityVisitor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 Defines an attachment capability of a (logical) infrastructure device node (e.g., {@link BlockStorage} node).
 */
@EqualsAndHashCode
@ToString
public class AttachmentCapability extends Capability {

    public AttachmentCapability(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    @Override
    public void accept(CapabilityVisitor v) {
        v.visit(this);
    }
}
