package org.opentosca.toscana.model.requirement;

import java.util.Set;

import org.opentosca.toscana.model.capability.Capability;
import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.relation.DependsOn;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
public class Dependency extends Requirement<Capability, RootNode, DependsOn> {

    @Builder
    protected Dependency(Capability capability,
                         Range occurrence,
                         @Singular Set<RootNode> fulfillers,
                         DependsOn relationship) {
        super(capability, occurrence, fulfillers, relationship);
    }

    public static DependencyBuilder builder(DependsOn relationship) {
        return new DependencyBuilder()
            .relationship(relationship);
    }

    public static class DependencyBuilder extends RequirementBuilder<Capability, RootNode, DependsOn> {
    }
}
