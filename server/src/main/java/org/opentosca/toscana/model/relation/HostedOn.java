package org.opentosca.toscana.model.relation;

import org.opentosca.toscana.model.visitor.RelationshipVisitor;

import lombok.Builder;
import lombok.Data;

/**
 Represents a hosting relationship between two nodes.
 (TOSCA Simple Profile in YAML Version 1.1, p. 160)
 */
@Data
public class HostedOn extends RootRelationship {

    public HostedOn() {
        super(null);
    }

    @Builder
    protected HostedOn(String description) {
        super(description);
    }

    public static HostedOn getFallback(HostedOn rel) {
        return (rel == null) ? new HostedOn() : rel;
    }

    @Override
    public void accept(RelationshipVisitor v) {
        v.visit(this);
    }

    public static class HostedOnBuilder extends RootRelationshipBuilder {
    }
}
