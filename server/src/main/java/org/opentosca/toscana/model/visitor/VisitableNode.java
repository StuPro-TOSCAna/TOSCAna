package org.opentosca.toscana.model.visitor;

public interface VisitableNode {

    public void accept(NodeVisitor v);
}
