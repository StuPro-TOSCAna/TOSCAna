package org.opentosca.toscana.plugins.kubernetes;

import java.io.IOException;

import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.plugins.lifecycle.AbstractLifecycle;

public class KubernetesLifecycle extends AbstractLifecycle {
    /**
     @param context because the context is always needed this should never be null
     It probably gets called by the <code>getInstance</code> method of the LifecycleAwarePlugin
     */
    public KubernetesLifecycle(TransformationContext context) throws IOException {
        super(context);
    }

    @Override
    public boolean checkModel() {
        return false;
    }

    @Override
    public void prepare() {

    }

    @Override
    public void transform() {

    }

    @Override
    public void cleanup() {

    }
}
