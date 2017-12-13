package org.opentosca.toscana.model.requirement;

import java.util.Set;

import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.ContainerRuntime;
import org.opentosca.toscana.model.relation.HostedOn;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
public class ContainerHostRequirement extends Requirement<ContainerCapability, ContainerRuntime, HostedOn> {

    @Builder
    protected ContainerHostRequirement(ContainerCapability capability,
                                       Range occurrence,
                                       @Singular Set<ContainerRuntime> fulfillers,
                                       HostedOn relationship) {
        super(capability, occurrence, fulfillers, relationship);
    }

    public static ContainerHostRequirementBuilder builder(ContainerCapability capability, 
                                                         HostedOn relationship) {
        return new ContainerHostRequirementBuilder()
            .capability(capability)
            .relationship(relationship);
    }
}
