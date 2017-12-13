package org.opentosca.toscana.model.capability;

import java.util.Objects;
import java.util.Set;

import org.opentosca.toscana.model.DescribableEntity;
import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.visitor.VisitableCapability;

import lombok.Data;
import lombok.NonNull;
import lombok.Singular;

/**
 Defines a set of data that can be associated with a {@link RootNode} to describe its capability or feature.
 (TOSCA Simple Profile in YAML Version 1.1, p. 82)
 */
@Data
public abstract class Capability extends DescribableEntity implements VisitableCapability {

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
    private final Range occurence;

    protected Capability(@Singular Set<Class<? extends RootNode>> validSourceTypes,
                         Range occurence,
                         String description) {
        super(description);
        this.validSourceTypes = Objects.requireNonNull(validSourceTypes);
        this.occurence = (occurence != null) ? occurence : Range.AT_LEAST_ONCE;
    }
}
