package org.opentosca.toscana.model.requirement;

import java.util.Optional;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.capability.Capability;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.relation.DependsOn;
import org.opentosca.toscana.model.util.ToscaKey;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class Dependency extends Requirement<Capability, RootNode, DependsOn> {

    public ToscaKey<DependsOn> RELATIONSHIP = new ToscaKey<>(RELATIONSHIP_NAME)
        .type(DependsOn.class);

    public Dependency(MappingEntity mappingEntity) {
        super(mappingEntity);
        setDefault(RELATIONSHIP, new DependsOn(getChildEntity(RELATIONSHIP)));
    }

    /**
     @return {@link #RELATIONSHIP}
     */
    public Optional<DependsOn> getRelationship() {
        return Optional.ofNullable(get(RELATIONSHIP));
    }

    /**
     Sets {@link #RELATIONSHIP}
     */
    @Override
    public Dependency setRelationship(DependsOn relationship) {
        set(RELATIONSHIP, relationship);
        return this;
    }
}
