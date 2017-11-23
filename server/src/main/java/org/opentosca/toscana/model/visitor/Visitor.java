package org.opentosca.toscana.model.visitor;

public interface Visitor {

    default void visit(Visitable v) {
        throw new UnsupportedTypeException(Object.class);
    }
}
