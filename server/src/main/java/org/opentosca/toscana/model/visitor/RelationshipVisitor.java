package org.opentosca.toscana.model.visitor;

import org.opentosca.toscana.model.relation.AttachesTo;
import org.opentosca.toscana.model.relation.ConnectsTo;
import org.opentosca.toscana.model.relation.DependsOn;
import org.opentosca.toscana.model.relation.HostedOn;
import org.opentosca.toscana.model.relation.RootRelationship;
import org.opentosca.toscana.model.relation.RoutesTo;

public interface RelationshipVisitor extends Visitor {

    default void visit(RootRelationship relation) {
        throw new UnsupportedTypeException(RootRelationship.class);
    }

    default void visit(AttachesTo relation) {
        throw new UnsupportedTypeException(AttachesTo.class);
    }

    default void visit(ConnectsTo relation) {
        throw new UnsupportedTypeException(ConnectsTo.class);
    }

    default void visit(DependsOn relation) {
        throw new UnsupportedTypeException(DependsOn.class);
    }

    default void visit(HostedOn relation) {
        throw new UnsupportedTypeException(HostedOn.class);
    }

    default void visit(RoutesTo relation) {
        throw new UnsupportedTypeException(RoutesTo.class);
    }
}
