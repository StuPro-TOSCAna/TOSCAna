package org.opentosca.toscana.model.requirement;

import java.util.Set;

import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.datatype.Range;
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
        super(ContainerCapability.getFallback(capability), occurrence,
            fulfillers, HostedOn.getFallback(relationship));
    }

    public static ContainerHostRequirement getFallback(ContainerHostRequirement r) {
        return (r == null) ? builder().build() : r;
    }
}
