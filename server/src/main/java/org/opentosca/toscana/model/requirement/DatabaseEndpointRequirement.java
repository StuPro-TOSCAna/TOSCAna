package org.opentosca.toscana.model.requirement;

import java.util.Optional;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.capability.DatabaseEndpointCapability;
import org.opentosca.toscana.model.node.Database;
import org.opentosca.toscana.model.relation.ConnectsTo;
import org.opentosca.toscana.model.util.ToscaKey;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class DatabaseEndpointRequirement extends Requirement<DatabaseEndpointCapability, Database, ConnectsTo> {
    public ToscaKey<DatabaseEndpointCapability> CAPABILITY = new ToscaKey<>(CAPABILITY_NAME)
        .type(DatabaseEndpointCapability.class);
    public ToscaKey<Database> NODE = new ToscaKey<>(NODE_NAME)
        .type(Database.class);
    public ToscaKey<ConnectsTo> RELATIONSHIP = new ToscaKey<>(RELATIONSHIP_NAME)
        .type(ConnectsTo.class);

    public DatabaseEndpointRequirement(MappingEntity mappingEntity) {
        super(mappingEntity);
        setDefault(RELATIONSHIP, new ConnectsTo(getChildEntity(RELATIONSHIP)));
    }

    /**
     @return {@link #CAPABILITY}
     */
    public DatabaseEndpointCapability getCapability() {
        return get(CAPABILITY);
    }

    /**
     Sets {@link #CAPABILITY}
     */
    public DatabaseEndpointRequirement setCapability(DatabaseEndpointCapability capability) {
        set(CAPABILITY, capability);
        return this;
    }

    /**
     @return {@link #NODE}
     */
    public Optional<Database> getNode() {
        return Optional.ofNullable(get(NODE));
    }

    /**
     Sets {@link #NODE}
     */
    public DatabaseEndpointRequirement setNode(Database node) {
        set(NODE, node);
        return this;
    }

    /**
     @return {@link #RELATIONSHIP}
     */
    public Optional<ConnectsTo> getRelationship() {
        return Optional.ofNullable(get(RELATIONSHIP));
    }

    /**
     Sets {@link #RELATIONSHIP}
     */
    @Override
    public DatabaseEndpointRequirement setRelationship(ConnectsTo relationship) {
        set(RELATIONSHIP, relationship);
        return this;
    }
}
