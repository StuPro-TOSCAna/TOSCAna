package org.opentosca.toscana.model.operation;

import java.util.Optional;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.util.ToscaKey;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 Defines the essential, normative operations that each TOSCA Relationship may support.
 (TOSCA Simple Profile in YAML Version 1.1, p. 165)
 */
@EqualsAndHashCode
@ToString
public class ConfigureLifecycle extends Interface {

    /**
     Operation to pre-configure the source endpoint.
     (TOSCA Simple Profile in YAML Version 1.1, p. 165)
     */
    public static ToscaKey<Operation> PRE_CONFIGURE_SOURCE = new ToscaKey<>("pre_configure_source").type(Operation.class);

    /**
     Operation to pre-configure the target endpoint.
     (TOSCA Simple Profile in YAML Version 1.1, p. 165)
     */
    public static ToscaKey<Operation> PRE_CONFIGURE_TARGET = new ToscaKey<>("pre_configure_target").type(Operation.class);

    /**
     Operation to post-configure the source endpoint.
     (TOSCA Simple Profile in YAML Version 1.1, p. 165)
     */
    public static ToscaKey<Operation> POST_CONFIGURE_SOURCE = new ToscaKey<>("post_configure_source").type(Operation.class);

    /**
     Operation to post-configure the target endpoint.
     (TOSCA Simple Profile in YAML Version 1.1, p. 165)
     */
    public static ToscaKey<Operation> POST_CONFIGURE_TARGET = new ToscaKey<>("post_configure_target").type(Operation.class);

    /**
     Operation to notify the source node of a target node being added via a relationship.
     (TOSCA Simple Profile in YAML Version 1.1, p. 165)
     */
    public static ToscaKey<Operation> ADD_TARGET = new ToscaKey<>("add_target").type(Operation.class);

    /**
     Operation to notify the target node of a source node which is now available via a relationship.
     (TOSCA Simple Profile in YAML Version 1.1, p. 165)
     */
    public static ToscaKey<Operation> ADD_SOURCE = new ToscaKey<>("add_source").type(Operation.class);

    /**
     Operation to notify source some property or attribute of the target changed.
     (TOSCA Simple Profile in YAML Version 1.1, p. 165)
     */
    public static ToscaKey<Operation> TARGET_CHANGED = new ToscaKey<>("target_changed").type(Operation.class);

    /**
     Operation to remove a target node.
     (TOSCA Simple Profile in YAML Version 1.1, p. 165)
     */
    public static ToscaKey<Operation> REMOVE_TARGET = new ToscaKey<>("remove_target").type(Operation.class);

    public ConfigureLifecycle(MappingEntity entity) {
        super(entity);
    }

    /**
     @return {@link #PRE_CONFIGURE_SOURCE}
     */

    public Optional<Operation> getPreConfigureSource() {
        return Optional.ofNullable(get(PRE_CONFIGURE_SOURCE));
    }

    /**
     Sets {@link #PRE_CONFIGURE_SOURCE}
     */
    public ConfigureLifecycle setPreConfigureSource(Operation preConfigureSource) {
        set(PRE_CONFIGURE_SOURCE, preConfigureSource);
        return this;
    }

    /**
     @return {@link #PRE_CONFIGURE_TARGET}
     */

    public Optional<Operation> getPreConfigureTarget() {
        return Optional.ofNullable(get(PRE_CONFIGURE_TARGET));
    }

    /**
     Sets {@link #PRE_CONFIGURE_TARGET}
     */
    public ConfigureLifecycle setPreConfigureTarget(Operation preConfigureTarget) {
        set(PRE_CONFIGURE_TARGET, preConfigureTarget);
        return this;
    }

    /**
     @return {@link #POST_CONFIGURE_SOURCE}
     */

    public Optional<Operation> getPostConfigureSource() {
        return Optional.ofNullable(get(POST_CONFIGURE_SOURCE));
    }

    /**
     Sets {@link #POST_CONFIGURE_SOURCE}
     */
    public ConfigureLifecycle setPostConfigureSource(Operation postConfigureSource) {
        set(POST_CONFIGURE_SOURCE, postConfigureSource);
        return this;
    }

    /**
     @return {@link #POST_CONFIGURE_TARGET}
     */

    public Optional<Operation> getPostConfigureTarget() {
        return Optional.ofNullable(get(POST_CONFIGURE_TARGET));
    }

    /**
     Sets {@link #POST_CONFIGURE_TARGET}
     */
    public ConfigureLifecycle setPostConfigureTarget(Operation postConfigureTarget) {
        set(POST_CONFIGURE_TARGET, postConfigureTarget);
        return this;
    }

    /**
     @return {@link #ADD_TARGET}
     */

    public Optional<Operation> getAddTarget() {
        return Optional.ofNullable(get(ADD_TARGET));
    }

    /**
     Sets {@link #ADD_TARGET}
     */
    public ConfigureLifecycle setAddTarget(Operation addTarget) {
        set(ADD_TARGET, addTarget);
        return this;
    }

    /**
     @return {@link #ADD_SOURCE}
     */

    public Optional<Operation> getAddSource() {
        return Optional.ofNullable(get(ADD_SOURCE));
    }

    /**
     Sets {@link #ADD_SOURCE}
     */
    public ConfigureLifecycle setAddSource(Operation addSource) {
        set(ADD_SOURCE, addSource);
        return this;
    }

    /**
     @return {@link #TARGET_CHANGED}
     */

    public Optional<Operation> getTargetChanged() {
        return Optional.ofNullable(get(TARGET_CHANGED));
    }

    /**
     Sets {@link #TARGET_CHANGED}
     */
    public ConfigureLifecycle setTargetChanged(Operation targetChanged) {
        set(TARGET_CHANGED, targetChanged);
        return this;
    }

    /**
     @return {@link #REMOVE_TARGET}
     */

    public Optional<Operation> getRemoveTarget() {
        return Optional.ofNullable(get(REMOVE_TARGET));
    }

    /**
     Sets {@link #REMOVE_TARGET}
     */
    public ConfigureLifecycle setRemoveTarget(Operation removeTarget) {
        set(REMOVE_TARGET, removeTarget);
        return this;
    }
}

