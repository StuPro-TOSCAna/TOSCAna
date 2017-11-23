package org.opentosca.toscana.model.relation;

import org.opentosca.toscana.model.visitor.Visitor;

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

    public static class DependsOnBuilder extends RootRelationshipBuilder {
    }
    
    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
