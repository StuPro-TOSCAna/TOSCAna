package org.opentosca.toscana.model.requirement;

import java.util.Set;

import org.opentosca.toscana.model.capability.DatabaseEndpointCapability;
import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.Database;
import org.opentosca.toscana.model.relation.ConnectsTo;

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

    public static DatabaseEndpointRequirementBuilder builder(ConnectsTo relationship) {
        return new DatabaseEndpointRequirementBuilder()
            .relationship(relationship);
    }

    public static Requirement<DatabaseEndpointCapability, Database, ConnectsTo> getFallback(Requirement<DatabaseEndpointCapability, Database, ConnectsTo> databaseEndpoint) {
        return (databaseEndpoint == null) ? DatabaseEndpointRequirement.builder(new ConnectsTo()).build() : databaseEndpoint;
    }

    public static class DatabaseEndpointRequirementBuilder extends RequirementBuilder<DatabaseEndpointCapability, Database, ConnectsTo> {
    }
}
