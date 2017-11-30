package org.opentosca.toscana.model.visitor;

public interface VisitableCapability {

    public void accept(CapabilityVisitor v);
}
