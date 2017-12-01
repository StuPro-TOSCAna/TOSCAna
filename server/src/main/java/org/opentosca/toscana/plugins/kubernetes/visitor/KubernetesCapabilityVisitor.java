package org.opentosca.toscana.plugins.kubernetes.visitor;

import java.util.List;

import org.opentosca.toscana.model.capability.AdminEndpointCapability;
import org.opentosca.toscana.model.capability.BindableCapability;
import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.capability.EndpointCapability;
import org.opentosca.toscana.model.capability.NodeCapability;
import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.model.capability.ScalableCapability;
import org.opentosca.toscana.model.capability.StorageCapability;
import org.opentosca.toscana.model.visitor.CapabilityVisitor;
import org.opentosca.toscana.plugins.kubernetes.visitor.policies.CapabilityPolicy;
import org.opentosca.toscana.plugins.kubernetes.visitor.policies.OsPolicy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KubernetesCapabilityVisitor implements CapabilityVisitor {
    private final static Logger logger = LoggerFactory.getLogger(KubernetesCapabilityVisitor.class.getName());

    private List<CapabilityPolicy> policies;

    @Override
    public void visit(EndpointCapability capability) {
        logger.info("Visiting the Endpoint capability.");
    }

    @Override
    public void visit(ScalableCapability capability) {
        logger.info("Visiting the Scalable capability.");
    }

    @Override
    public void visit(StorageCapability capability) {
        logger.info("Visiting the Storage capability.");
    }

    @Override
    public void visit(ContainerCapability capability) {

    }

    @Override
    public void visit(NodeCapability capability) {

    }

    @Override
    public void visit(AdminEndpointCapability capability) {

    }

    @Override
    public void visit(BindableCapability capability) {
        
    }

    @Override
    public void visit(OsCapability capability) {
        logger.info("Visiting the OS capability.");
        OsCapability.Type capabilityType = capability.getType().get();
        for (CapabilityPolicy policy : policies) {
            if (policy instanceof OsPolicy) {
                List<OsCapability.Type> policyArchitecture = ((OsPolicy) policy).getUnsupportedTypes();
                if (policyArchitecture.contains(capabilityType)) {
                    logger.warn("Unsupported OS Type: " + capabilityType.name());
                } else {
                    logger.info("Everything ok");
                }
            }
        }
    }

    public void setPolicies(List<CapabilityPolicy> policies) {
        this.policies = policies;
    }
}
