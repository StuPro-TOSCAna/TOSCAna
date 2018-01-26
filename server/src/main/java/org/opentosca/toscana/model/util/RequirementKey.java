package org.opentosca.toscana.model.util;

import org.opentosca.toscana.model.capability.Capability;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.relation.RootRelationship;
import org.opentosca.toscana.model.requirement.Requirement;

public class RequirementKey<T> extends ToscaKey<T> {

    public final static String FULFILLER = "fulfiller_type";
    public final static String CAPABILITY = "capability_type";
    public final static String RELATIONSHIP = "relationship_type";

    public RequirementKey(String name) {
        super(RootNode.REQUIREMENTS, name);
        super.type(Requirement.class);
    }

    public <T> RequirementKey<T> subTypes(Class<? extends Capability> capabilityType, Class<? extends RootNode> fulfillerType, Class<? extends RootRelationship> relationshipType) {
        directive(FULFILLER, fulfillerType);
        directive(CAPABILITY, capabilityType);
        directive(RELATIONSHIP, relationshipType);
        return (RequirementKey<T>) this;
    }
}
