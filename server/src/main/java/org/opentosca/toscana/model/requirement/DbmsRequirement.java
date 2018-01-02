package org.opentosca.toscana.model.requirement;

import java.util.Set;

import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.Dbms;
import org.opentosca.toscana.model.relation.HostedOn;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
public class DbmsRequirement extends Requirement<ContainerCapability, Dbms, HostedOn> {

    @Builder
    protected DbmsRequirement(ContainerCapability capability,
                              Range occurrence,
                              @Singular Set<Dbms> fulfillers,
                              HostedOn relationship) {
        super(capability, occurrence, fulfillers, HostedOn.getFallback(relationship));
    }

    public static Requirement<ContainerCapability, Dbms, HostedOn> getFallback(Requirement<ContainerCapability, Dbms, HostedOn> host) {
        return (host == null) ? DbmsRequirement.builder().build() : host;
    }

    public static class DbmsRequirementBuilder extends RequirementBuilder<ContainerCapability, Dbms, HostedOn> {
    }
}
