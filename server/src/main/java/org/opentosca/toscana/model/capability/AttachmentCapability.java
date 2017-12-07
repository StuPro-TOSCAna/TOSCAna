package org.opentosca.toscana.model.capability;

import java.util.Set;

import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.BlockStorage;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.visitor.CapabilityVisitor;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

/**
 Defines an attachment capability of a (logical) infrastructure device node (e.g., {@link BlockStorage} node).
 */
@Data
public class AttachmentCapability extends Capability {

    @Builder
    protected AttachmentCapability(Set<Class<? extends RootNode>> validSourceTypes,
                                   Range occurence,
                                   String description) {
        super(validSourceTypes, occurence, description);
    }

    @Override
    public void accept(CapabilityVisitor v) {
        v.visit(this);
    }

    public static AttachmentCapability getFallback(AttachmentCapability c) {
        return (c == null) ? AttachmentCapability.builder().build() : c;
    }

    public static class AttachmentCapabilityBuilder extends CapabilityBuilder {
    }
}
