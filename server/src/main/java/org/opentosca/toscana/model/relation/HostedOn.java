package org.opentosca.toscana.model.relation;

import org.opentosca.toscana.model.visitor.Visitor;

import lombok.Builder;
import lombok.Data;

/**
 Represents a hosting relationship between two nodes.
 (TOSCA Simple Profile in YAML Version 1.1, p. 160)
 */
@Data
public class HostedOn extends RootRelationship {

    @Builder
    protected HostedOn(String description) {
        super(description);
    }

    public static class HostedOnBuilder extends RootRelationshipBuilder {
    }
    
    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
