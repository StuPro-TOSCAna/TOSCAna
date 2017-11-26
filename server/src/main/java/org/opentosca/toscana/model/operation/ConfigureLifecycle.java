package org.opentosca.toscana.model.operation;

import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Sets;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

/**
 Defines the essential, normative operations that each TOSCA Relationship may support.
 (TOSCA Simple Profile in YAML Version 1.1, p. 165)
 */
@Data
public class ConfigureLifecycle extends Interface {

    /**
     Operation to pre-configure the source endpoint.
     (TOSCA Simple Profile in YAML Version 1.1, p. 165)
     */
    private final Operation preConfigureSource;

    /**
     Operation to pre-configure the target endpoint.
     (TOSCA Simple Profile in YAML Version 1.1, p. 165)
     */
    private final Operation preConfigureTarget;

    /**
     Operation to post-configure the source endpoint.
     (TOSCA Simple Profile in YAML Version 1.1, p. 165)
     */
    private final Operation postConfigureSource;

    /**
     Operation to post-configure the target endpoint.
     (TOSCA Simple Profile in YAML Version 1.1, p. 165)
     */
    private final Operation postConfigureTarget;

    /**
     Operation to notify the source node of a target node being added via a relationship.
     (TOSCA Simple Profile in YAML Version 1.1, p. 165)
     */
    private final Operation addTarget;

    /**
     Operation to notify the target node of a source node which is now available via a relationship.
     (TOSCA Simple Profile in YAML Version 1.1, p. 165)
     */
    private final Operation addSource;

    /**
     Operation to notify source some property or attribute of the target changed.
     (TOSCA Simple Profile in YAML Version 1.1, p. 165)
     */
    private final Operation targetChanged;

    /**
     Operation to remove a target node.
     (TOSCA Simple Profile in YAML Version 1.1, p. 165)
     */
    private final Operation removeTarget;

    @Builder
    private ConfigureLifecycle(Operation preConfigureSource,
                               Operation preConfigureTarget,
                               Operation postConfigureSource,
                               Operation postConfigureTarget,
                               Operation addTarget,
                               Operation addSource,
                               Operation targetChanged,
                               Operation removeTarget,
                               @Singular Set<OperationVariable> inputs) {
        super(inputs,
            Sets.newHashSet(preConfigureSource, preConfigureTarget, postConfigureSource,
                postConfigureTarget, addTarget, addSource, targetChanged, removeTarget));
        this.preConfigureSource = preConfigureSource;
        this.preConfigureTarget = preConfigureTarget;
        this.postConfigureSource = postConfigureSource;
        this.postConfigureTarget = postConfigureTarget;
        this.addTarget = addTarget;
        this.addSource = addSource;
        this.targetChanged = targetChanged;
        this.removeTarget = removeTarget;
    }

    /**
     @return {@link #preConfigureSource}
     */
    public Optional<Operation> getPreConfigureSource() {
        return Optional.ofNullable(preConfigureSource);
    }

    /**
     @return {@link #preConfigureTarget}
     */
    public Optional<Operation> getPreConfigureTarget() {
        return Optional.ofNullable(preConfigureTarget);
    }

    /**
     @return {@link #postConfigureSource}
     */
    public Optional<Operation> getPostConfigureSource() {
        return Optional.ofNullable(postConfigureSource);
    }

    /**
     @return {@link #postConfigureTarget}
     */
    public Optional<Operation> getPostConfigureTarget() {
        return Optional.ofNullable(postConfigureTarget);
    }

    /**
     @return {@link #addTarget}
     */
    public Optional<Operation> getAddTarget() {
        return Optional.ofNullable(addTarget);
    }

    /**
     @return {@link #addSource}
     */
    public Optional<Operation> getAddSource() {
        return Optional.ofNullable(addSource);
    }

    /**
     @return {@link #targetChanged}
     */
    public Optional<Operation> getTargetChanged() {
        return Optional.ofNullable(targetChanged);
    }

    /**
     @return {@link #removeTarget}
     */
    public Optional<Operation> getRemoveTarget() {
        return Optional.ofNullable(removeTarget);
    }

    public static class ConfigureLifecycleBuilder extends InterfaceBuilder {
    }
}
