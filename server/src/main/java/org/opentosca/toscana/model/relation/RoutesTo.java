package org.opentosca.toscana.model.relation;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.visitor.RelationshipVisitor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 Represents an intentional network routing between two Endpoints in different networks.
 <p>
 (TOSCA Simple Profile in YAML Version 1.1, p. 162)
 */
@EqualsAndHashCode
@ToString
public class RoutesTo extends RootRelationship {

    public RoutesTo(MappingEntity entity) {
        super(entity);
    }

    @Override
    public void accept(RelationshipVisitor v) {
        v.visit(this);
    }
}
