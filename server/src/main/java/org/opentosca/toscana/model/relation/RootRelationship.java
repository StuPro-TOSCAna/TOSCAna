package org.opentosca.toscana.model.relation;

import java.util.UUID;

import org.opentosca.toscana.core.parse.graphconverter.MappingEntity;
import org.opentosca.toscana.model.DescribableEntity;
import org.opentosca.toscana.model.visitor.RelationshipVisitor;
import org.opentosca.toscana.model.visitor.VisitableRelationship;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 The default TOSCA Relationship Type that all other TOSCA Relationships derive from.
 (TOSCA Simple Profile in YAML Version 1.1, p. 159)
 */
@EqualsAndHashCode
@ToString
public class RootRelationship extends DescribableEntity implements VisitableRelationship {

    // todo still needed?
    private final String uid = UUID.randomUUID().toString();

    public RootRelationship(MappingEntity entity) {
        super(entity);
    }

    @Override
    public void accept(RelationshipVisitor v) {
    }
}
