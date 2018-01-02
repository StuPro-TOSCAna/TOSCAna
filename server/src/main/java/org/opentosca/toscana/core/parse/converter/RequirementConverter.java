package org.opentosca.toscana.core.parse.converter;

import java.util.List;

import org.opentosca.toscana.model.capability.Capability;
import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.relation.RootRelationship;
import org.opentosca.toscana.model.requirement.Requirement;

import org.eclipse.winery.model.tosca.yaml.TRequirementAssignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequirementConverter {

    private final static Logger logger = LoggerFactory.getLogger(RequirementConverter.class.getName());

    public <CapabilityT extends Capability, NodeT extends RootNode, RelationshipT extends RootRelationship>
    RequirementConversion<CapabilityT, NodeT, RelationshipT> convert(TRequirementAssignment requirementAssignment, Class relationshipType) {
        RelationshipT relationship = new RelationshipConverter().convert(requirementAssignment.getRelationship(), relationshipType);
        Requirement<CapabilityT, NodeT, RelationshipT> requirement = Requirement
            .<CapabilityT, NodeT, RelationshipT>builder(relationship)
            .occurrence(getOccurrence(requirementAssignment))
            .build();
        String fulfiller = requirementAssignment.getNode().getLocalPart();
        return new RequirementConversion(requirement, fulfiller);
    }

    private Range getOccurrence(TRequirementAssignment node) {
        List<String> occurrenceDefinition = node.getOccurrences();
        Range occurrence = null;
        if (occurrenceDefinition != null && occurrenceDefinition.size() == 2) {
            int min = Integer.valueOf(occurrenceDefinition.get(0));
            int max = Integer.valueOf(occurrenceDefinition.get(1));
            occurrence = new Range(min, max);
        }
        return occurrence;
    }
}
