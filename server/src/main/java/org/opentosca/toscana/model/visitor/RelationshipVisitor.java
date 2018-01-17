package org.opentosca.toscana.model.visitor;

import org.opentosca.toscana.model.relation.AttachesTo;
import org.opentosca.toscana.model.relation.ConnectsTo;
import org.opentosca.toscana.model.relation.DependsOn;
import org.opentosca.toscana.model.relation.HostedOn;
import org.opentosca.toscana.model.relation.RootRelationship;
import org.opentosca.toscana.model.relation.RoutesTo;

public interface RelationshipVisitor {

    default void visit(AttachesTo relation) {
        // noop
    }

    default void visit(ConnectsTo relation) {
        // noop
    }

    default void visit(DependsOn relation) {
        // noop
    }

    default void visit(HostedOn relation) {
        // noop
    }

    default void visit(RoutesTo relation) {
        // noop
    }

    default void visit(RootRelationship relation) {
        // noop;
    }
}
