package org.opentosca.toscana.core.parse.converter;

import org.opentosca.toscana.model.capability.Capability;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.relation.RootRelationship;
import org.opentosca.toscana.model.requirement.Requirement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 Temporary data class which holds information about a specific requirement assignment during conversion
 */
public class RequirementConversion<CapabilityT extends Capability, NodeT extends RootNode, RelationshipT extends RootRelationship> {

    private final static Logger logger = LoggerFactory.getLogger(RequirementConversion.class.getName());

    public final Requirement<CapabilityT, NodeT, RelationshipT> requirement;
    public final String fulfiller;

    public RequirementConversion(Requirement<CapabilityT, NodeT, RelationshipT> requirement, String fulfiller) {
        this.requirement = requirement;
        this.fulfiller = fulfiller;
    }
}
