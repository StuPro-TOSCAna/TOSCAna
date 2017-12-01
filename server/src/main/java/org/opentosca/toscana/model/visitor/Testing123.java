package org.opentosca.toscana.model.visitor;

import org.opentosca.toscana.model.capability.Capability;

public class Testing123 {

    public static void main(String[] args) {
        Capability c = Capability.builder().build();

        CapabilityVisitor sv = new CapabilityVisitor() {
        };

        StrictCapabilityVisitor v = new StrictCapabilityVisitor() {
        };
        
        c.accept(sv);
//        c.accept(v);
    }
}
