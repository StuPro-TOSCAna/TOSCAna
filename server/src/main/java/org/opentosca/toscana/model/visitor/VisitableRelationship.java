package org.opentosca.toscana.model.visitor;

public interface VisitableRelationship {

    public void accept(RelationshipVisitor v);
}
