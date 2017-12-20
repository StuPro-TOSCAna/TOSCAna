package org.opentosca.toscana.model.relation;

import org.opentosca.toscana.model.visitor.RelationshipVisitor;

import lombok.Builder;
import lombok.Data;

/**
 Represents a general dependency relationship between two nodes.
 */
@Data
public class DependsOn extends RootRelationship {

    public DependsOn() {
        super(null);
    }

    @Builder
    protected DependsOn(String description) {
        super(description);
    }

    public static RootRelationship getFallback(RootRelationship rel) {
        return (rel == null) ? builder().build() : rel;
    }

    @Override
    public void accept(RelationshipVisitor v) {
        v.visit(this);
    }

    public static class DependsOnBuilder extends RootRelationshipBuilder {
    }
}
