package org.opentosca.toscana.model.capability;

import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.visitor.CapabilityVisitor;

import lombok.Builder;
import lombok.Data;

import static java.lang.String.format;

/**
 The default TOSCA type that should be used to express a scalability capability of a node.
 (TOSCA Simple Profile in YAML Version 1.1, p. 158)
 */
@Data
public class ScalableCapability extends Capability {

    /**
     Indicates the minimum and maximum number of instances that should be created
     for the associated TOSCA Node Template by a TOSCA orchestrator.
     (TOSCA Simple Profile in YAML Version 1.1, p. 157)
     <p>
     Defaults to {@link Range#EXACTLY_ONCE}.
     */
    private final Range scaleRange;

    /**
     The optional default number of instances that should be the starting number of instances
     a TOSCA orchestrator should attempt to allocate.
     (TOSCA Simple Profile in YAML Version 1.1, p. 157)
     */
    private final Integer defaultInstances;

    @Builder
    protected ScalableCapability(Range scaleRange,
                                 Integer defaultInstances,
                                 Set<Class<? extends RootNode>> validSourceTypes,
                                 Range occurrence) {
        super(validSourceTypes, occurrence);
        this.scaleRange = (scaleRange == null) ? Range.EXACTLY_ONCE : scaleRange;
        this.defaultInstances = defaultInstances;
        if (defaultInstances != null && !scaleRange.inRange(defaultInstances)) {
            throw new IllegalArgumentException(format(
                "Constraint violation: range.min (%d) <= defaultInstances (%d) <= range.max (%d)",
                scaleRange.min, defaultInstances, scaleRange.max));
        }
    }

    /**
     @return {@link #defaultInstances}
     */
    public Optional<Integer> getDefaultInstances() {
        return Optional.ofNullable(defaultInstances);
    }

    @Override
    public void accept(CapabilityVisitor v) {
        v.visit(this);
    }

    public static class ScalableCapabilityBuilder extends CapabilityBuilder {
    }
}
