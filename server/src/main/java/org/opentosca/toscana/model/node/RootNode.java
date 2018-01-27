package org.opentosca.toscana.model.node;

import java.util.HashSet;
import java.util.Set;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.DescribableEntity;
import org.opentosca.toscana.model.artifact.Artifact;
import org.opentosca.toscana.model.capability.Capability;
import org.opentosca.toscana.model.operation.Interface;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.requirement.Requirement;
import org.opentosca.toscana.model.util.ToscaKey;
import org.opentosca.toscana.model.visitor.VisitableNode;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 The base node. Every other node will derive from this class.
 */
@EqualsAndHashCode
@ToString
public abstract class RootNode extends DescribableEntity implements VisitableNode {

    public static final ToscaKey<String> TYPE = new ToscaKey<>("type");

    public static ToscaKey<Requirement> REQUIREMENTS = new ToscaKey<>("requirements")
        .type(Requirement.class).list();
    public static ToscaKey<Capability> CAPABILITIES = new ToscaKey<>("capabilities")
        .type(Capability.class);
    public static ToscaKey<Interface> INTERFACES = new ToscaKey<>("interfaces")
        .type(Interface.class);
    public static ToscaKey<StandardLifecycle> STANDARD_LIFECYCLE = new ToscaKey<>(INTERFACES, "Standard")
        .type(StandardLifecycle.class);

    public static ToscaKey<Artifact> ARTIFACTS = new ToscaKey<>("artifacts")
        .type(Artifact.class);

    public RootNode(MappingEntity mappingEntity) {
        super(mappingEntity);
        setDefault(STANDARD_LIFECYCLE, new StandardLifecycle(getChildEntity(STANDARD_LIFECYCLE)));
    }

    /**
     @return {@link #REQUIREMENTS}
     */
    public Set<Requirement> getRequirements() {
        return new HashSet<>(getCollection(REQUIREMENTS));
    }

    /**
     @return {@link #CAPABILITIES}
     */
    public Set<Capability> getCapabilities() {
        return new HashSet<>(getCollection(CAPABILITIES));
    }

    /**
     @return {@link #STANDARD_LIFECYCLE}
     */
    public StandardLifecycle getStandardLifecycle() {
        return get(STANDARD_LIFECYCLE);
    }

    /**
     Sets {@link #STANDARD_LIFECYCLE}
     */
    public RootNode setStandardLifecycle(StandardLifecycle standardLifecycle) {
        set(STANDARD_LIFECYCLE, standardLifecycle);
        return this;
    }

    /**
     @return {@link #ARTIFACTS}
     */
    public Set<Artifact> getArtifacts() {
        return new HashSet<>(getCollection(ARTIFACTS));
    }

    /**
     @return {@link #INTERFACES}
     */
    public Set<Interface> getInterfaces() {
        return new HashSet<>(getCollection(INTERFACES));
    }
}

