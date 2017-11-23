package org.opentosca.toscana.model.visitor;

public interface Visitable {
    
    public void accept(Visitor v);
}
