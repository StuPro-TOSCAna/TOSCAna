package org.opentosca.toscana.plugins.kubernetes.visitor;

import java.util.Arrays;
import java.util.Set;

import org.opentosca.toscana.model.capability.Capability;
import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.ContainerRuntime;
import org.opentosca.toscana.model.node.DockerApplication;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.visitor.NodeVisitor;
import org.opentosca.toscana.plugins.kubernetes.visitor.policies.OsPolicy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KubernetesNodeVisitor implements NodeVisitor {
    private final static Logger logger = LoggerFactory.getLogger(KubernetesNodeVisitor.class.getName());

    private KubernetesVisitorPolicy activePolicy;

    @Override
    public void visit(DockerApplication node) {
        logger.info("Visiting the docker application named: {}", node.getNodeName());
        visitFulfillers(node.host.getFulfillers());
    }

    private void visitFulfillers(Set fulfillers) {
        for (Object fulfiller : fulfillers) {
            RootNode rootNode = (RootNode) fulfiller;
            rootNode.accept(this);
        }
    }

    @Override
    public void visit(ContainerRuntime node) {
        logger.info("Visiting the container runtime");
    }

    @Override
    public void visit(Compute node) {
        if (activePolicy == KubernetesVisitorPolicy.MODEL_CHECK) {
            checkOperatingSystem(node.getCapabilities());
        }
    }

    private void checkOperatingSystem(Set<Capability> capabilities) {
        KubernetesCapabilityVisitor visitor = new KubernetesCapabilityVisitor();
        visitor.setPolicies(Arrays.asList(new OsPolicy(Arrays.asList(OsCapability.Type.WINDOWS))));
        capabilities.forEach(capability -> capability.accept(visitor));
    }

    public void setPolicy(KubernetesVisitorPolicy policy) {
        activePolicy = policy;
    }
}
