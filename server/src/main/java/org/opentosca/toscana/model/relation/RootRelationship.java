package org.opentosca.toscana.model.relation;

import org.opentosca.toscana.core.parse.model.MappingEntity;
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
public abstract class RootRelationship extends DescribableEntity implements VisitableRelationship {

    public RootRelationship(MappingEntity entity) {
        super(entity);
    }

    @Override
    public void accept(RelationshipVisitor v) {
        v.visit(this);
    }
}
