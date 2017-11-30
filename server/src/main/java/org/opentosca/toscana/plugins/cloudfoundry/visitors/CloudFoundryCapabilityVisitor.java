package org.opentosca.toscana.plugins.cloudfoundry.visitors;

import org.opentosca.toscana.model.capability.ComputeCapability;
import org.opentosca.toscana.model.visitor.CapabilityVisitor;
import org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryApplication;

public class CloudFoundryCapabilityVisitor implements CapabilityVisitor {
    
    private CloudFoundryApplication app;
    
    public CloudFoundryCapabilityVisitor(CloudFoundryApplication app){
        this.app = app;
    }
    
    @Override
    public void visit(ComputeCapability capability) {
        
    }
}
