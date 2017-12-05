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
        super(capability, occurrence, fulfillers, relationship);
    }

    public static DbmsRequirementBuilder builder(ContainerCapability capability,
                                                 HostedOn relationship) {
        return new DbmsRequirementBuilder()
            .capability(capability)
            .relationship(relationship);
    }
}
