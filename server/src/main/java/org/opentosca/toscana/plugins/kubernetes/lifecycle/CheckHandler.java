package org.opentosca.toscana.plugins.kubernetes.lifecycle;

import java.util.Set;

import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.plugins.kubernetes.exceptions.UnsupportedOsTypeException;
import org.opentosca.toscana.plugins.kubernetes.visitor.check.NodeTypeCheckVisitor;
import org.opentosca.toscana.plugins.kubernetes.visitor.check.OsCheckNodeVisitor;

public class CheckHandler extends LifecycleHandler {

    CheckHandler(KubernetesLifecycle lifecycle) {
        super(lifecycle);
    }

    public boolean checkModel() {
        Set<RootNode> nodes = lifecycle.model.getNodes();
        boolean nodeTypeCheck = checkNodeTypes(nodes);
        boolean osTypeCheck = checkOsType(nodes);
        return nodeTypeCheck && osTypeCheck;
    }

    /**
     Checks if the model contains a unsupported os

     @param nodes - Nodes to be checked
     @return boolean - true if successful, false otherwise
     */
    private boolean checkOsType(Set<RootNode> nodes) {
        OsCheckNodeVisitor nodeVisitor = new OsCheckNodeVisitor(logger);
        for (RootNode node : nodes) {
            try {
                node.accept(nodeVisitor);
            } catch (UnsupportedOsTypeException e) {
                logger.warn(e.getMessage(), e);
                return false;
            }
        }
        return true;
    }

    /**
     Checks if there are any unsupported node types

     @param nodes - Nodes to be checked
     @return boolean - true if successful, false otherwise
     */
    private boolean checkNodeTypes(Set<RootNode> nodes) {
        for (RootNode node : nodes)
            try {
                node.accept(new NodeTypeCheckVisitor());
            } catch (UnsupportedOperationException e) {
                logger.warn("Transformation of the type {} is not supported", node.getClass().getName(), e);
                return false;
            }
        return true;
    }
}
