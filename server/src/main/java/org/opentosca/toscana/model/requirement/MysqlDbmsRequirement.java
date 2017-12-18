package org.opentosca.toscana.model.requirement;

import java.util.Set;

import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.MysqlDbms;
import org.opentosca.toscana.model.relation.HostedOn;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
public class MysqlDbmsRequirement extends Requirement<ContainerCapability, MysqlDbms, HostedOn> {

    @Builder
    protected MysqlDbmsRequirement(ContainerCapability capability,
                                   Range occurrence,
                                   @Singular Set<MysqlDbms> fulfillers,
                                   HostedOn relationship) {
        super(capability, occurrence,
            fulfillers, HostedOn.getFallback(relationship));
    }

    public static class MysqlDbmsRequirementBuilder extends RequirementBuilder<ContainerCapability, MysqlDbms, HostedOn> {
    }
}
