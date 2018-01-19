package org.opentosca.toscana.model.requirement;

import java.util.Optional;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.capability.AttachmentCapability;
import org.opentosca.toscana.model.node.BlockStorage;
import org.opentosca.toscana.model.relation.AttachesTo;
import org.opentosca.toscana.model.util.ToscaKey;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class BlockStorageRequirement extends Requirement<AttachmentCapability, BlockStorage, AttachesTo> {

    public ToscaKey<AttachmentCapability> CAPABILITY = new ToscaKey<>(CAPABILITY_NAME)
        .type(AttachmentCapability.class);
    public ToscaKey<BlockStorage> NODE = new ToscaKey<>(NODE_NAME)
        .type(BlockStorage.class);
    public ToscaKey<AttachesTo> RELATIONSHIP = new ToscaKey<>(RELATIONSHIP_NAME)
        .type(AttachesTo.class);

    public BlockStorageRequirement(MappingEntity mappingEntity) {
        super(mappingEntity);
        setDefault(RELATIONSHIP, new AttachesTo(getChildEntity(RELATIONSHIP)));
    }

    /**
     @return {@link #CAPABILITY}
     */
    public AttachmentCapability getCapability() {
        return get(CAPABILITY);
    }

    /**
     Sets {@link #CAPABILITY}
     */
    public BlockStorageRequirement setCapability(AttachmentCapability capability) {
        set(CAPABILITY, capability);
        return this;
    }

    /**
     @return {@link #NODE}
     */
    public Optional<BlockStorage> getNode() {
        return Optional.ofNullable(get(NODE));
    }

    /**
     Sets {@link #NODE}
     */
    public BlockStorageRequirement setNode(BlockStorage node) {
        set(NODE, node);
        return this;
    }

    /**
     @return {@link #RELATIONSHIP}
     */
    public Optional<AttachesTo> getRelationship() {
        return Optional.ofNullable(get(RELATIONSHIP));
    }

    /**
     Sets {@link #RELATIONSHIP}
     */
    @Override
    public BlockStorageRequirement setRelationship(AttachesTo relationship) {
        set(RELATIONSHIP, relationship);
        return this;
    }
}
