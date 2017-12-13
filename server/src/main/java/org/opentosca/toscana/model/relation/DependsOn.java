package org.opentosca.toscana.model.relation;

import org.opentosca.toscana.model.visitor.RelationshipVisitor;

import lombok.Builder;
import lombok.Data;

/**
 Represents a general dependency relationship between two nodes.
 */
@Data
public class DependsOn extends RootRelationship {

    @Builder
    protected DependsOn(String description) {
        super(description);
    }

    @Override
    public void accept(RelationshipVisitor v) {
        v.visit(this);
    }
}
