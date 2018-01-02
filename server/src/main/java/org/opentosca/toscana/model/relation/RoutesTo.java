package org.opentosca.toscana.model.relation;

import org.opentosca.toscana.model.visitor.RelationshipVisitor;

import lombok.Builder;
import lombok.Data;

/**
 Represents an intentional network routing between two Endpoints in different networks.
 <p>
 (TOSCA Simple Profile in YAML Version 1.1, p. 162)
 */
@Data
public class RoutesTo extends RootRelationship {

    public RoutesTo() {
        super(null);
    }

    @Builder
    protected RoutesTo(String description) {
        super(description);
    }

    @Override
    public void accept(RelationshipVisitor v) {
        v.visit(this);
    }

    public static class RoutesToBuilder extends RootRelationshipBuilder {
    }
}
