package org.opentosca.toscana.model.visitor;

import org.opentosca.toscana.model.relation.AttachesTo;
import org.opentosca.toscana.model.relation.ConnectsTo;
import org.opentosca.toscana.model.relation.DependsOn;
import org.opentosca.toscana.model.relation.HostedOn;
import org.opentosca.toscana.model.relation.RootRelationship;
import org.opentosca.toscana.model.relation.RoutesTo;

/**
 Unimplemented methods throw an {@link UnsupportedTypeException} when invoked.
 */
public interface StrictRelationshipVisitor extends RelationshipVisitor {

    @Override
    default void visit(AttachesTo relation) {
        throw new UnsupportedTypeException(AttachesTo.class);
    }

    @Override
    default void visit(ConnectsTo relation) {
        throw new UnsupportedTypeException(ConnectsTo.class);
    }

    @Override
    default void visit(DependsOn relation) {
        throw new UnsupportedTypeException(DependsOn.class);
    }

    @Override
    default void visit(HostedOn relation) {
        throw new UnsupportedTypeException(HostedOn.class);
    }

    @Override
    default void visit(RoutesTo relation) {
        throw new UnsupportedTypeException(RoutesTo.class);
    }

    @Override
    default void visit(RootRelationship relation) {
        throw new UnsupportedTypeException(RootRelationship.class);
    }
}
