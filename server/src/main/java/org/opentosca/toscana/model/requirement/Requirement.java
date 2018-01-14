package org.opentosca.toscana.model.requirement;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.model.capability.Capability;
import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.relation.RootRelationship;

import lombok.Builder;
import lombok.Data;

// note: breaking Sun's naming convention recommendation for Generic Type Names in favour of new Google Style (append T)

/**
 A dependency of a Node which needs to be fulfilled by a matching {@link Capability}.
 The requirement may itself include the specific Node Instance (explicitly)
 or provide an abstract type, that a TOSCA orchestrator can use to fulfill the capability (implicitly).
 (TOSCA Simple Profile in YAML Version 1.1, p. 84)

 @param <CapabilityT>   the Capability Type which is needs to be fulfilled
 @param <NodeT>         the Node Type which must be used to fulfill the requirement
 @param <RelationshipT> the Relationship Type which is constructed by fulfilling the requirement */
@Data
public class Requirement<CapabilityT extends Capability, NodeT extends RootNode, RelationshipT extends RootRelationship> {

    /**
     The Relationship to construct when fulfilling the requirement.
     (TOSCA Simple Profile in YAML Version 1.1, p. 85)
     */
    protected final RelationshipT relationship;
    /**
     The optional minimum and maximum occurrences for the requirement.
     (TOSCA Simple Profile in YAML Version 1.1, p. 85)
     <p>
     Note: Defaults to {@link Range#EXACTLY_ONCE}
     */
    protected Range occurrence;
    /**
     The optional Capability that can fulfill this requirement.
     (TOSCA Simple Profile in YAML Version 1.1, p. 85)
     */
    protected CapabilityT capability;
    /**
     The optional Nodes that have the required capability and shall be used to fulfill this requirement.
     (TOSCA Simple Profile in YAML Version 1.1, p. 85)
     */
    protected Set<NodeT> fulfillers;

    @Builder
    protected Requirement(CapabilityT capability,
                          Range occurrence,
                          Set<NodeT> fulfillers,
                          RelationshipT relationship) {
        this.capability = capability;
        this.occurrence = (occurrence == null) ? Range.EXACTLY_ONCE : occurrence;
        this.fulfillers = Objects.requireNonNull(fulfillers);
        this.relationship = Objects.requireNonNull(relationship);
    }

    /**
     @param relationship {@link #relationship}
     */
    public static <CapabilityT extends Capability, NodeT extends RootNode, RelationshipT extends RootRelationship>
    RequirementBuilder<CapabilityT, NodeT, RelationshipT> builder(RelationshipT relationship) {
        return new RequirementBuilder<CapabilityT, NodeT, RelationshipT>()
            .relationship(relationship);
    }

    /**
     @return {@link #capability}
     */
    public Optional<CapabilityT> getCapability() {
        return Optional.ofNullable(capability);
    }

    public static class RequirementBuilder<CapabilityT extends Capability, NodeT extends RootNode, RelationshipT extends RootRelationship> {
        private HashSet<NodeT> fulfillers = new HashSet<>();

        public RequirementBuilder<CapabilityT, NodeT, RelationshipT> fulfiller(NodeT fulfiller) {
            if (this.fulfillers == null) this.fulfillers = new HashSet<NodeT>();
            this.fulfillers.add(fulfiller);
            return this;
        }

        public RequirementBuilder<CapabilityT, NodeT, RelationshipT> clearFulfillers() {
            if (this.fulfillers != null) {
                this.fulfillers.clear();
            }
            return this;
        }
    }
    // TODO add some kind of isFulfilled():boolean method.
}
