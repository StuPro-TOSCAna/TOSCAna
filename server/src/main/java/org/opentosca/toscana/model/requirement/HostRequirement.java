package org.opentosca.toscana.model.requirement;

import java.util.Set;

import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.relation.HostedOn;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
public class HostRequirement extends Requirement<ContainerCapability, Compute, HostedOn> {

    @Builder
    protected HostRequirement(ContainerCapability capability,
                              Range occurrence,
                              @Singular Set<Compute> fulfillers,
                              HostedOn relationship) {
        super(ContainerCapability.getFallback(capability), occurrence,
            fulfillers, HostedOn.getFallback(relationship));
    }

    public static HostRequirement getFallback(HostRequirement host) {
        return (host == null) ? HostRequirement.builder().build() : host;
    }
}
