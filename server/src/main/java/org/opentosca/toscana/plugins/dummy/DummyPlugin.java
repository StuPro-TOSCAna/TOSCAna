package org.opentosca.toscana.plugins.dummy;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.plugins.lifecycle.LifecycleAwarePlugin;

public class DummyPlugin extends LifecycleAwarePlugin<DummyLifecycle> {

    private boolean fail;
    
    public DummyPlugin(Platform platform, boolean fail) {
        super(platform);
        this.fail = fail;
    }

    @Override
    protected Set<Class<?>> getSupportedNodeTypes() {
        return new HashSet<>();
    }

    @Override
    protected DummyLifecycle getInstance(TransformationContext context) throws IOException {
        return new DummyLifecycle(context, fail);
    }
}
