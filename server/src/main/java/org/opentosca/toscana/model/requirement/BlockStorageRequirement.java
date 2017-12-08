package org.opentosca.toscana.model.requirement;

import java.util.Set;

import org.opentosca.toscana.model.capability.AttachmentCapability;
import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.BlockStorage;
import org.opentosca.toscana.model.relation.AttachesTo;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
public class BlockStorageRequirement extends Requirement<AttachmentCapability, BlockStorage, AttachesTo> {

    @Builder
    protected BlockStorageRequirement(AttachmentCapability capability,
                                      Range occurrence,
                                      @Singular Set<BlockStorage> fulfillers,
                                      AttachesTo relationship) {
        super(AttachmentCapability.getFallback(capability), occurrence, fulfillers, relationship);
    }

    public static BlockStorageRequirementBuilder builder(AttachesTo relationship) {
        return new BlockStorageRequirementBuilder()
            .relationship(relationship);
    }
}
