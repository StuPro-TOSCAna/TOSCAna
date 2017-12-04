package org.opentosca.toscana.model.requirement;

import java.util.Set;

import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.capability.EndpointCapability;
import org.opentosca.toscana.model.capability.StorageCapability;
import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.relation.HostedOn;
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
        super(capability, occurrence, fulfillers, relationship);
    }
    
    public static EndpointRequirementBuilder builder(EndpointCapability capability, 
                                                     RootRelationship relationship){
        return new EndpointRequirementBuilder()
            .capability(capability)
            .relationship(relationship);
    }
}
