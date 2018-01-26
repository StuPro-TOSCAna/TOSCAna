package org.opentosca.toscana.model.requirement;

import java.util.Optional;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.capability.EndpointCapability;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.relation.DependsOn;
import org.opentosca.toscana.model.relation.RootRelationship;
import org.opentosca.toscana.model.util.ToscaKey;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class NetworkRequirement extends Requirement<EndpointCapability, RootNode, DependsOn> {

    public ToscaKey<EndpointCapability> CAPABILITY = new ToscaKey<>(CAPABILITY_NAME)
        .type(EndpointCapability.class);

    public NetworkRequirement(MappingEntity mappingEntity) {
        super(mappingEntity);
        setDefault(RELATIONSHIP, new DependsOn(getChildEntity(RELATIONSHIP)));
    }

    /**
     @return {@link #CAPABILITY}
     */
    public Optional<EndpointCapability> getCapability() {
        return Optional.ofNullable(get(CAPABILITY));
    }

    /**
     Sets {@link #CAPABILITY}
     */
    public NetworkRequirement setCapability(EndpointCapability capability) {
        set(CAPABILITY, capability);
        return this;
    }
}
