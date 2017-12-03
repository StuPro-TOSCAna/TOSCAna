package org.opentosca.toscana.model.requirement;

import java.util.Set;

import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.relation.HostedOn;

import lombok.Builder;
import lombok.Data;

@Data
public class HostRequirement extends Requirement<ContainerCapability, Compute, HostedOn> {

    @Builder
    protected HostRequirement(ContainerCapability capability, Range occurrence, Set<Compute> fulfillers, HostedOn relationship) {
        super(capability, occurrence, fulfillers, relationship);
    }
    
    public static HostRequirementBuilder builder(ContainerCapability capability, HostedOn relationship){
        return new HostRequirementBuilder()
            .capability(capability)
            .relationship(relationship);
        
    }

    public static class HostRequirementBuilder extends RequirementBuilder<ContainerCapability, Compute, HostedOn> {
    }
}
