package org.opentosca.toscana.model.requirement;

import java.util.Set;

import org.opentosca.toscana.model.capability.StorageCapability;
import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.relation.RootRelationship;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
public class StorageRequirement extends Requirement<StorageCapability, RootNode, RootRelationship> {

    @Builder
    protected StorageRequirement(StorageCapability capability,
                                 Range occurrence,
                                 @Singular Set<RootNode> fulfillers,
                                 RootRelationship relationship) {
        super(capability, occurrence, fulfillers, relationship);
    }

    public static StorageRequirementBuilder builder(StorageCapability capability,
                                                    RootRelationship relationship) {
        return new StorageRequirementBuilder()
            .capability(capability)
            .relationship(relationship);
    }
}
