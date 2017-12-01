package org.opentosca.toscana.plugins.kubernetes;

import java.io.IOException;
import java.util.Set;

import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.plugins.kubernetes.visitor.KubernetesNodeVisitor;
import org.opentosca.toscana.plugins.kubernetes.visitor.KubernetesVisitorPolicy;
import org.opentosca.toscana.plugins.lifecycle.AbstractLifecycle;

public class KubernetesLifecycle extends AbstractLifecycle {

    private EffectiveModel model;

    /**
     @param context because the context is always needed this should never be null
     It probably gets called by the <code>getInstance</code> method of the LifecycleAwarePlugin
     */
    public KubernetesLifecycle(TransformationContext context) throws IOException {
        super(context);
        model = context.getModel();
    }

    @Override
    public boolean checkModel() {
        Set<RootNode> nodes = model.getNodes();
        KubernetesNodeVisitor nodeVisitor = new KubernetesNodeVisitor();
        nodeVisitor.setPolicy(KubernetesVisitorPolicy.MODEL_CHECK);
        nodes.forEach(rootNode -> rootNode.accept(nodeVisitor));
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
