package org.opentosca.toscana.plugins.kubernetes.visitor;

import java.util.Set;

import org.opentosca.toscana.model.node.DockerApplication;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KubernetesNodeVisitor implements NodeVisitor {
    private final static Logger logger = LoggerFactory.getLogger(KubernetesNodeVisitor.class.getName());

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
}
