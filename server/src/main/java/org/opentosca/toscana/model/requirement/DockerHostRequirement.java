package org.opentosca.toscana.model.requirement;

import java.util.Optional;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.capability.DockerContainerCapability;
import org.opentosca.toscana.model.node.ContainerRuntime;
import org.opentosca.toscana.model.relation.HostedOn;
import org.opentosca.toscana.model.util.ToscaKey;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class DockerHostRequirement extends Requirement<DockerContainerCapability, ContainerRuntime, HostedOn> {
    public ToscaKey<DockerContainerCapability> CAPABILITY = new ToscaKey<>(CAPABILITY_NAME)
        .type(DockerContainerCapability.class);
    public ToscaKey<ContainerRuntime> NODE = new ToscaKey<>(NODE_NAME)
        .type(ContainerRuntime.class);
    public ToscaKey<HostedOn> RELATIONSHIP = new ToscaKey<>(RELATIONSHIP_NAME)
        .type(HostedOn.class);

    public DockerHostRequirement(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    /**
     @return {@link #CAPABILITY}
     */
    public DockerContainerCapability getCapability() {
        return get(CAPABILITY);
    }

    /**
     Sets {@link #CAPABILITY}
     */
    public DockerHostRequirement setCapability(DockerContainerCapability capability) {
        set(CAPABILITY, capability);
        return this;
    }

    /**
     @return {@link #NODE}
     */
    public Optional<ContainerRuntime> getNode() {
        return Optional.ofNullable(get(NODE));
    }

    /**
     Sets {@link #NODE}
     */
    public DockerHostRequirement setNode(ContainerRuntime node) {
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
    public DockerHostRequirement setRelationship(HostedOn relationship) {
        set(RELATIONSHIP, relationship);
        return this;
    }
}
