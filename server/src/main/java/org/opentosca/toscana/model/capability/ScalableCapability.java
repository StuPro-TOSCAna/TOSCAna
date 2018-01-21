package org.opentosca.toscana.model.capability;

import java.util.Optional;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.util.ToscaKey;
import org.opentosca.toscana.model.visitor.CapabilityVisitor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 The default TOSCA type that should be used to express a scalability capability of a node.
 (TOSCA Simple Profile in YAML Version 1.1, p. 158)
 */
@ToString
@EqualsAndHashCode
public class ScalableCapability extends Capability {

    /**
     Indicates the maximum number of instances that should be created for the associated node.
     <p>
     Same as the max value of the Range connected to #SCALE_RANGE
     */
    public static ToscaKey<Integer> MAX_INSTANCES = new ToscaKey<>(PROPERTIES, "min_instances");

    /**
     Indicates the minimum number of instances that should be created for the associated node.
     <p>
     Same as the min value of the Range connected to #SCALE_RANGE
     */
    public static ToscaKey<Integer> MIN_INSTANCES = new ToscaKey<>(PROPERTIES, "max_instances");

    /**
     The optional default number of instances that should be the starting number of instances
     a TOSCA orchestrator should attempt to allocate.
     (TOSCA Simple Profile in YAML Version 1.1, p. 157)
     */
    public static ToscaKey<Integer> DEFAULT_INSTANCES = new ToscaKey<>(PROPERTIES, "default_instances");

    public ScalableCapability(MappingEntity mappingEntity) {
        super(mappingEntity);
        init();
    }

    private void init() {
        setDefault(MAX_INSTANCES, 1);
        setDefault(MIN_INSTANCES, 1);
    }

    /**
     @return {@link #MAX_INSTANCES}
     */
    public Integer getMaxInstances() {
        return get(MAX_INSTANCES);
    }

    /**
     Sets {@link #MAX_INSTANCES}
     */
    public ScalableCapability setMaxInstances(Integer maxInstances) {
        set(MAX_INSTANCES, maxInstances);
        return this;
    }

    /**
     @return {@link #MIN_INSTANCES}
     */
    public Integer getMinInstances() {
        return get(MIN_INSTANCES);
    }

    /**
     Sets {@link #MIN_INSTANCES}
     */
    public ScalableCapability setMinInstances(Integer minInstances) {
        set(MIN_INSTANCES, minInstances);
        return this;
    }

    /**
     @return {@link #DEFAULT_INSTANCES}
     */
    public Optional<Integer> getDefaultInstances() {
        return Optional.ofNullable(get(DEFAULT_INSTANCES));
    }

    /**
     Sets {@link #DEFAULT_INSTANCES}
     */
    public ScalableCapability setDefaultInstances(Integer defaultInstances) {
        set(DEFAULT_INSTANCES, defaultInstances);
        return this;
    }

    @Override
    public void accept(CapabilityVisitor v) {
        v.visit(this);
    }
}
