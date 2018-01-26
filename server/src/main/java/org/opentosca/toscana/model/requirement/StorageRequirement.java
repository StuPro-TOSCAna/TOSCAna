package org.opentosca.toscana.model.requirement;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.capability.StorageCapability;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.relation.DependsOn;
import org.opentosca.toscana.model.relation.RootRelationship;
import org.opentosca.toscana.model.util.ToscaKey;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class StorageRequirement extends Requirement<StorageCapability, RootNode, DependsOn> {
    public ToscaKey<StorageCapability> CAPABILITY = new ToscaKey<>(CAPABILITY_NAME)
        .type(StorageCapability.class);

    public StorageRequirement(MappingEntity mappingEntity) {
        super(mappingEntity);
        setDefault(RELATIONSHIP, new DependsOn(getChildEntity(RELATIONSHIP)));
    }

    /**
     @return {@link #CAPABILITY}
     */
    public StorageCapability getCapability() {
        return get(CAPABILITY);
    }

    /**
     Sets {@link #CAPABILITY}
     */
    public StorageRequirement setCapability(StorageCapability capability) {
        set(CAPABILITY, capability);
        return this;
    }
}
