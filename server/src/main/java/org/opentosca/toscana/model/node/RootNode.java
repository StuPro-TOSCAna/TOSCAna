package org.opentosca.toscana.model.node;

import java.util.Set;

import org.opentosca.toscana.core.parse.graphconverter.MappingEntity;
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

    // TODO what kind of class is SimpleProperty and Attribute?
    public static ToscaKey<Requirement> REQUIREMENTS = new ToscaKey<>("requirements")
        .type(Requirement.class);
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
        return getCollection(REQUIREMENTS);
    }

    /**
     @return {@link #CAPABILITIES}
     */
    public Set<Capability> getCapabilities() {
        // TODO rework this. Client needs to get specific types of capabilities -- but toscafactory instanciates plain Capabilities..?
        // needs to be done in order for capability visitor to work properly
        // in theory, type information is stored in the toscakeys..
        return getCollection(CAPABILITIES);
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
        return getCollection(ARTIFACTS);
    }

    /**
     @return {@link #INTERFACES}
     */
    public Set<Interface> getInterfaces() {
        return getCollection(INTERFACES);
    }
}

