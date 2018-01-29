package org.opentosca.toscana.model.capability;

import java.util.HashSet;
import java.util.Set;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.BaseToscaElement;
import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.util.ToscaKey;
import org.opentosca.toscana.model.visitor.VisitableCapability;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 Defines a set of data that can be associated with a {@link RootNode} to describe its capability or feature.
 (TOSCA Simple Profile in YAML Version 1.1, p. 82)
 */
@EqualsAndHashCode
@ToString
public abstract class Capability extends BaseToscaElement implements VisitableCapability {

    /**
     Set of Node Class Types that are valid sources of any relationship established to this capability.
     If empty, all Node classes are valid source types.
     (TOSCA Simple Profile in YAML Version 1.1, p. 83)
     */
    public static ToscaKey<Class<? extends RootNode>> VALID_SOURCE_TYPES = new ToscaKey<>("valid_source_types")
        .type(Class.class);

    /**
     Specifies how many requirements (for this capability) can be fulfilled by this capability.
     <p>
     Defaults to {@link Range#AT_LEAST_ONCE}
     */
    private Range occurrence = Range.AT_LEAST_ONCE;

    protected Capability(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    /**
     @return {@link #VALID_SOURCE_TYPES}
     */
    public Set<Class<? extends RootNode>> getValidSourceTypes() {
        return new HashSet<>(getCollection(VALID_SOURCE_TYPES));
    }

    public Range getOccurrence() {
        return occurrence;
    }

    public Capability setOccurrence(Range occurrence) {
        this.occurrence = occurrence;
        return this;
    }
}
