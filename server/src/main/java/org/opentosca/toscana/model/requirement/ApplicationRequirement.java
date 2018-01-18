package org.opentosca.toscana.model.requirement;

import java.util.Optional;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.capability.EndpointCapability;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.relation.RoutesTo;
import org.opentosca.toscana.model.util.ToscaKey;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class ApplicationRequirement extends Requirement<EndpointCapability, RootNode, RoutesTo> {

    public ToscaKey<EndpointCapability> CAPABILITY = new ToscaKey<>(CAPABILITY_NAME)
        .type(EndpointCapability.class);
    public ToscaKey<RoutesTo> RELATIONSHIP = new ToscaKey<>(RELATIONSHIP_NAME)
        .type(RoutesTo.class);

    public ApplicationRequirement(MappingEntity mappingEntity) {
        super(mappingEntity);
        setDefault(RELATIONSHIP, new RoutesTo(getChildEntity(RELATIONSHIP)));
    }

    /**
     @return {@link #RELATIONSHIP}
     */
    public Optional<RoutesTo> getRelationship() {
        return Optional.ofNullable(get(RELATIONSHIP));
    }

    /**
     Sets {@link #RELATIONSHIP}
     */
    @Override
    public ApplicationRequirement setRelationship(RoutesTo relationship) {
        set(RELATIONSHIP, relationship);
        return this;
    }

    /**
     @return {@link #CAPABILITY}
     */
    public EndpointCapability getCapability() {
        return get(CAPABILITY);
    }

    /**
     Sets {@link #CAPABILITY}
     */
    public ApplicationRequirement setCapability(EndpointCapability capability) {
        set(CAPABILITY, capability);
        return this;
    }
}
