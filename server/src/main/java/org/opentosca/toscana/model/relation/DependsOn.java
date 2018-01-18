package org.opentosca.toscana.model.relation;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.visitor.RelationshipVisitor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 Represents a general dependency relationship between two nodes.
 */
@EqualsAndHashCode
@ToString
public class DependsOn extends RootRelationship {

    public DependsOn(MappingEntity entity) {
        super(entity);
    }

    @Override
    public void accept(RelationshipVisitor v) {
        v.visit(this);
    }
}
