package org.opentosca.toscana.model.requirement;

import java.util.Set;

import org.opentosca.toscana.model.capability.EndpointCapability;
import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.relation.DependsOn;
import org.opentosca.toscana.model.relation.RootRelationship;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
public class EndpointRequirement extends Requirement<EndpointCapability, RootNode, RootRelationship> {

    @Builder
    protected EndpointRequirement(EndpointCapability capability,
                                  Range occurrence,
                                  @Singular Set<RootNode> fulfillers,
                                  RootRelationship relationship) {
        super(capability, occurrence, fulfillers, DependsOn.getFallback(relationship));
    }

    public static Requirement<EndpointCapability, RootNode, RootRelationship> getFallback(Requirement<EndpointCapability, RootNode, RootRelationship> network) {
        return (network == null) ? EndpointRequirement.builder().build() : network;
    }

    public static class EndpointRequirementBuilder extends RequirementBuilder<EndpointCapability, RootNode, RootRelationship> {
    }
}
