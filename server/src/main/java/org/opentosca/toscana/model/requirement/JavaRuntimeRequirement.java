package org.opentosca.toscana.model.requirement;

import java.util.Optional;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.node.custom.JavaRuntime;
import org.opentosca.toscana.model.relation.HostedOn;
import org.opentosca.toscana.model.util.ToscaKey;

public class JavaRuntimeRequirement extends Requirement<ContainerCapability, JavaRuntime, HostedOn> {

    public ToscaKey<ContainerCapability> CAPABILITY = new ToscaKey<>(CAPABILITY_NAME)
        .type(ContainerCapability.class);
    public ToscaKey<JavaRuntime> NODE = new ToscaKey<>(NODE_NAME)
        .type(JavaRuntime.class);
    public ToscaKey<HostedOn> RELATIONSHIP = new ToscaKey<>(RELATIONSHIP_NAME)
        .type(HostedOn.class);

    public JavaRuntimeRequirement(MappingEntity mappingEntity) {
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
    public JavaRuntimeRequirement setCapability(ContainerCapability capability) {
        set(CAPABILITY, capability);
        return this;
    }

    /**
     @return {@link #NODE}
     */
    public Optional<JavaRuntime> getNode() {
        return Optional.ofNullable(get(NODE));
    }

    /**
     Sets {@link #NODE}
     */
    public JavaRuntimeRequirement setNode(JavaRuntime node) {
        set(NODE, node);
        return this;
    }

    /**
     @return {@link #RELATIONSHIP}
     */
    @Override
    public Optional<HostedOn> getRelationship() {
        return Optional.ofNullable(get(RELATIONSHIP));
    }

    /**
     Sets {@link #RELATIONSHIP}
     */
    @Override
    public JavaRuntimeRequirement setRelationship(HostedOn relationship) {
        set(RELATIONSHIP, relationship);
        return this;
    }
}
