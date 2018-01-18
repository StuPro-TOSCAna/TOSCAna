package org.opentosca.toscana.model.requirement;

import java.util.Optional;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.node.Dbms;
import org.opentosca.toscana.model.relation.HostedOn;
import org.opentosca.toscana.model.util.ToscaKey;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class DbmsRequirement extends Requirement<ContainerCapability, Dbms, HostedOn> {

    public ToscaKey<ContainerCapability> CAPABILITY = new ToscaKey<>(CAPABILITY_NAME)
        .type(ContainerCapability.class);
    public ToscaKey<Dbms> NODE = new ToscaKey<>(NODE_NAME)
        .type(Dbms.class);
    public ToscaKey<HostedOn> RELATIONSHIP = new ToscaKey<>(RELATIONSHIP_NAME)
        .type(HostedOn.class);

    public DbmsRequirement(MappingEntity mappingEntity) {
        super(mappingEntity);
        setDefault(RELATIONSHIP, new HostedOn(getChildEntity(RELATIONSHIP)));
    }

    /**
     @return {@link #CAPABILITY}
     */
    public ContainerCapability getCapability() {
        return get(CAPABILITY);
    }

    /**
     Sets {@link #CAPABILITY}
     */
    public DbmsRequirement setCapability(ContainerCapability capability) {
        set(CAPABILITY, capability);
        return this;
    }

    /**
     @return {@link #NODE}
     */
    public Optional<Dbms> getNode() {
        return Optional.ofNullable(get(NODE));
    }

    /**
     Sets {@link #NODE}
     */
    public DbmsRequirement setNode(Dbms node) {
        set(NODE, node);
        return this;
    }

    /**
     @return {@link #RELATIONSHIP}
     */
    public Optional<HostedOn> getRelationship() {
        return Optional.ofNullable(get(RELATIONSHIP));
    }

    /**
     Sets {@link #RELATIONSHIP}
     */
    @Override
    public DbmsRequirement setRelationship(HostedOn relationship) {
        set(RELATIONSHIP, relationship);
        return this;
    }
}
