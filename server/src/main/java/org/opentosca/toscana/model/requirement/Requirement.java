package org.opentosca.toscana.model.requirement;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.BaseToscaElement;
import org.opentosca.toscana.model.capability.Capability;
import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.relation.DependsOn;
import org.opentosca.toscana.model.relation.RootRelationship;
import org.opentosca.toscana.model.util.ToscaKey;

import lombok.EqualsAndHashCode;
import lombok.ToString;

// note: breaking Sun's naming convention recommendation for Generic Type Names in favour of new Google Style (append T)

/**
 A dependency of a Node which needs to be fulfilled by a matching {@link Capability}.
 The requirement may itself include the specific Node Instance (explicitly)
 or provide an abstract type, that a TOSCA orchestrator can use to fulfill the capability (implicitly).
 (TOSCA Simple Profile in YAML Version 1.1, p. 84)

 @param <CapabilityT>   the Capability Type which is needs to be fulfilled
 @param <NodeT>         the Node Type which must be used to fulfill the requirement
 @param <RelationshipT> the Relationship Type which is constructed by fulfilling the requirement */
@EqualsAndHashCode
@ToString
public class Requirement<CapabilityT extends Capability, NodeT extends RootNode, RelationshipT extends RootRelationship> extends BaseToscaElement {
    

    public static String CAPABILITY_NAME = "capability";
    public static String NODE_NAME = "node";
    public static String RELATIONSHIP_NAME = "relationship";

    public RequirementType REQUIREMENT_TYPE = new RequirementType(Requirement.class, Capability.class, RootNode.class, RootRelationship.class);
    public ToscaKey<Capability> CAPABILITY = new ToscaKey<>(CAPABILITY_NAME)
        .type(Capability.class);
    public ToscaKey<? extends RootNode> NODE = new ToscaKey<>(NODE_NAME)
        .type(RootNode.class);
    public ToscaKey<RootRelationship> RELATIONSHIP = new ToscaKey<>(RELATIONSHIP_NAME)
        .type(RootRelationship.class);

    /**
     The Relationship to construct when fulfilling the requirement.
     (TOSCA Simple Profile in YAML Version 1.1, p. 85)
     */
    protected RelationshipT relationship;

    /**
     The optional Capability that can fulfill this requirement.
     (TOSCA Simple Profile in YAML Version 1.1, p. 85)
     */
    protected CapabilityT capability;

    /**
     The optional minimum and maximum occurrences for the requirement.
     (TOSCA Simple Profile in YAML Version 1.1, p. 85)
     <p>
     Note: Defaults to {@link Range#EXACTLY_ONCE}
     */
    protected Range occurrence = Range.EXACTLY_ONCE;

    /**
     The optional Nodes that have the required capability and shall be used to fulfill this requirement.
     (TOSCA Simple Profile in YAML Version 1.1, p. 85)
     */
    protected Set<NodeT> fulfillers = new HashSet<>();

    public Requirement(MappingEntity mappingEntity) {
        super(mappingEntity);
        setDefault(RELATIONSHIP, new DependsOn(getChildEntity(RELATIONSHIP)));
    }

    public Range getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(Range occurrence) {
        this.occurrence = occurrence;
    }

    /**
     @return {@link #fulfillers}
     */
    public Set<NodeT> getFulfillers() {
        Set<NodeT> fulfillers = new HashSet<>();
        NodeT fulfiller = (NodeT) get(NODE);
        if (fulfiller != null) {
            fulfillers.add(fulfiller);
        }
        return fulfillers;
    }

    /**
     @return {@link #relationship}
     */
    public Optional<RelationshipT> getRelationship() {
        return Optional.of((RelationshipT) get(RELATIONSHIP));
    }

    /**
     Sets {@link #relationship}
     */
    public Requirement setRelationship(RelationshipT relationship) {
        set(RELATIONSHIP, relationship);
        return this;
    }
    
    public static class RequirementType {
        public final Class<? extends Requirement> WRAPPER_TYPE;
        public final Class<? extends Capability> CAPABILITY_TYPE;
        public final Class<? extends RootNode> NODE_TYPE;
        public final Class<? extends RootRelationship> RELATIONSHIP_TYPE;

        public RequirementType(Class<? extends Requirement> wrapper, Class<? extends Capability> capability, Class<? extends RootNode> node, Class<? extends RootRelationship> relationship) {
            this.WRAPPER_TYPE = wrapper;
            this.CAPABILITY_TYPE = capability;
            this.NODE_TYPE = node;
            this.RELATIONSHIP_TYPE = relationship;
        }
    }
}
