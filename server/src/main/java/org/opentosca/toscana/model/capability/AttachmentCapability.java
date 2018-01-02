package org.opentosca.toscana.model.capability;

import java.util.Set;

import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.BlockStorage;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.visitor.CapabilityVisitor;

import lombok.Builder;
import lombok.Data;

/**
 Defines an attachment capability of a (logical) infrastructure device node (e.g., {@link BlockStorage} node).
 */
@Data
public class AttachmentCapability extends Capability {

    @Builder
    protected AttachmentCapability(Set<Class<? extends RootNode>> validSourceTypes,
                                   Range occurrence) {
        super(validSourceTypes, occurrence);
    }

    public static AttachmentCapability getFallback(AttachmentCapability c) {
        return (c == null) ? AttachmentCapability.builder().build() : c;
    }

    @Override
    public void accept(CapabilityVisitor v) {
        v.visit(this);
    }

    public static class AttachmentCapabilityBuilder extends CapabilityBuilder {
    }
}
