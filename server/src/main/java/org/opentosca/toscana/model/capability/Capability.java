package org.opentosca.toscana.model.capability;

import java.util.HashSet;
import java.util.Set;

import org.opentosca.toscana.model.AbstractEntity;
import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.visitor.VisitableCapability;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

/**
 Defines a set of data that can be associated with a {@link RootNode} to describe its capability or feature.
 (TOSCA Simple Profile in YAML Version 1.1, p. 82)
 */
@Data
public abstract class Capability extends AbstractEntity implements VisitableCapability {

    /**
     Set of Node Class Types that are valid sources of any relationship established to this capability.
     If empty, all Node classes are valid source types.
     (TOSCA Simple Profile in YAML Version 1.1, p. 83)
     */
    private final Set<Class<? extends RootNode>> validSourceTypes;

    /**
     Specifies how many requirements (for this capability) can be fulfilled by this capability.
     <p>
     Defaults to {@link Range#AT_LEAST_ONCE}
     */
    @NonNull
    private Range occurrence;

    @Builder
    protected Capability(Set<Class<? extends RootNode>> validSourceTypes,
                         Range occurrence) {
        this.validSourceTypes = (validSourceTypes == null) ? new HashSet<>() : validSourceTypes;
        this.occurrence = (occurrence != null) ? occurrence : Range.AT_LEAST_ONCE;
    }

    public static class CapabilityBuilder extends AbstractEntityBuilder {

        private Set<Class<? extends RootNode>> validSourceTypes = new HashSet<>();

        @Override
        public Capability build() {
            // should never be called (RootNode is abstract)
            throw new UnsupportedOperationException();
        }

        public CapabilityBuilder validSourceType(Class<? extends RootNode> sourceType) {
            validSourceTypes.add(sourceType);
            return this;
        }
    }
}
