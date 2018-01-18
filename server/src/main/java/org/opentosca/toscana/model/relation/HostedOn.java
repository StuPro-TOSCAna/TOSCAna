package org.opentosca.toscana.model.relation;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.visitor.RelationshipVisitor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 Represents a hosting relationship between two nodes.
 (TOSCA Simple Profile in YAML Version 1.1, p. 160)
 */
@EqualsAndHashCode
@ToString
public class HostedOn extends RootRelationship {

    public HostedOn(MappingEntity entity) {
        super(entity);
    }

    @Override
    public void accept(RelationshipVisitor v) {
        v.visit(this);
    }
}
