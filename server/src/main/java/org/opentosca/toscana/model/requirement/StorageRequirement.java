package org.opentosca.toscana.model.requirement;

import java.util.Set;

import org.opentosca.toscana.model.capability.StorageCapability;
import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.relation.DependsOn;
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
        super(StorageCapability.getFallback(capability), occurrence, fulfillers, relationship);
    }

    public static StorageRequirementBuilder builder(RootRelationship relationship) {
        return new StorageRequirementBuilder()
            .relationship(relationship);
    }

    public static StorageRequirement getFallback(StorageRequirement s) {
        return (s == null) ? StorageRequirement.builder(new DependsOn()).build() : s;
    }
}
