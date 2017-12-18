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
        super(capability, occurrence, fulfillers, relationship);
    }

    public static Requirement<AttachmentCapability, BlockStorage, AttachesTo> getFallback(Requirement<AttachmentCapability, BlockStorage, AttachesTo> storage) {
        return (storage == null) ? BlockStorageRequirement.builder(AttachesTo.builder("/").build()).build() : storage;
    }

    public static BlockStorageRequirementBuilder builder(AttachesTo relationship) {
        return new BlockStorageRequirementBuilder()
            .relationship(relationship);
    }

    public static class BlockStorageRequirementBuilder extends RequirementBuilder<AttachmentCapability, BlockStorage, AttachesTo> {
    }
}
