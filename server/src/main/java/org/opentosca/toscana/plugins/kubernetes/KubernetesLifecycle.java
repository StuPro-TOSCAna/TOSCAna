package org.opentosca.toscana.plugins.kubernetes;

import java.io.IOException;
import java.util.Set;

import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.plugins.kubernetes.exceptions.UnsupportedOsTypeException;
import org.opentosca.toscana.plugins.kubernetes.visitor.NodeTypeCheckVisitor;
import org.opentosca.toscana.plugins.kubernetes.visitor.OsCheckNodeVisitor;
import org.opentosca.toscana.plugins.lifecycle.AbstractLifecycle;

import org.slf4j.Logger;

public class KubernetesLifecycle extends AbstractLifecycle {

    private final EffectiveModel model;
    private final Logger logger;

    /**
     @param context because the context is always needed this should never be null
     It probably gets called by the <code>getInstance</code> method of the LifecycleAwarePlugin
     */
    public KubernetesLifecycle(TransformationContext context) throws IOException {
        super(context);
        model = context.getModel();
        logger = context.getLogger(KubernetesLifecycle.class);
    }

    @Override
    public boolean checkModel() {
        Set<RootNode> nodes = model.getNodes();
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
                logger.warn(e.getMessage());
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
                return false;
            }
        return true;
    }

    @Override
    public void prepare() {
        //throw new UnsupportedOperationException();
    }

    @Override
    public void transform() {
        //throw new UnsupportedOperationException();
    }

    @Override
    public void cleanup() {
        //throw new UnsupportedOperationException();
    }
}
