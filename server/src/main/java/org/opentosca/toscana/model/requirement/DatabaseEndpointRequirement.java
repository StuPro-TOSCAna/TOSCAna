package org.opentosca.toscana.model.requirement;

import java.util.Set;

import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.capability.DatabaseEndpointCapability;
import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.Database;
import org.opentosca.toscana.model.relation.ConnectsTo;
import org.opentosca.toscana.model.relation.HostedOn;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
public class DatabaseEndpointRequirement extends Requirement<DatabaseEndpointCapability, Database, ConnectsTo> {

    @Builder
    protected DatabaseEndpointRequirement(DatabaseEndpointCapability capability,
                                          Range occurrence,
                                          @Singular Set<Database> fulfillers,
                                          ConnectsTo relationship) {
        super(capability, occurrence, fulfillers, relationship);
    }

    public static DatabaseEndpointRequirementBuilder builder(DatabaseEndpointCapability capability,
                                                             ConnectsTo relationship) {
        return new DatabaseEndpointRequirementBuilder()
            .capability(capability)
            .relationship(relationship);
    }
}
