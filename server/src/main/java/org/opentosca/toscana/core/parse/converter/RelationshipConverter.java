package org.opentosca.toscana.core.parse.converter;

import org.opentosca.toscana.model.relation.AttachesTo;
import org.opentosca.toscana.model.relation.ConnectsTo;
import org.opentosca.toscana.model.relation.DependsOn;
import org.opentosca.toscana.model.relation.HostedOn;
import org.opentosca.toscana.model.relation.RootRelationship;
import org.opentosca.toscana.model.relation.RoutesTo;

import org.eclipse.winery.model.tosca.yaml.TRelationshipAssignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelationshipConverter {

    private final static Logger logger = LoggerFactory.getLogger(RelationshipConverter.class.getName());

    // TODO if relationshipassignment not null, use it to populate relationship

    public HostedOn convertHostedOn(TRelationshipAssignment relationshipAssignment) {
        return HostedOn.builder().build();
    }

    public DependsOn convertDependsOn(TRelationshipAssignment relationshipAssignment) {
        return DependsOn.builder().build();
    }

    public ConnectsTo convertConnectsTo(TRelationshipAssignment relationshipAssignment) {
        return ConnectsTo.builder().build();
    }

    public RoutesTo convertRoutesTo(TRelationshipAssignment relationshipAssignment) {
        return RoutesTo.builder().build();
    }

    public AttachesTo convertAttachesTo(TRelationshipAssignment relationshipAssignment) {
        return AttachesTo.builder("/").build();
    }

    public <RelationshipT extends RootRelationship> RelationshipT convert(TRelationshipAssignment relationshipAssignment, Class relationshipType) {
        switch (relationshipType.getSimpleName()) {
            case "HostedOn":
                return (RelationshipT) convertHostedOn(relationshipAssignment);
            case "DependsOn":
                return (RelationshipT) convertDependsOn(relationshipAssignment);
            case "RoutesTo":
                return (RelationshipT) convertRoutesTo(relationshipAssignment);
            case "AttachesTo":
                return (RelationshipT) convertAttachesTo(relationshipAssignment);
            case "ConnectsTo":
                return (RelationshipT) convertConnectsTo(relationshipAssignment);
            default:
                throw new IllegalStateException();
        }
    }
}
