package org.opentosca.toscana.core.testdata.dummyplugins;

import org.opentosca.toscana.core.plugin.lifecycle.AbstractLifecycle;
import org.opentosca.toscana.core.plugin.lifecycle.ToscanaPlugin;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.platform.Platform;

public class DummyPlugin extends ToscanaPlugin {

    public DummyPlugin(Platform platform) {
        super(platform);
    }

    @Override
    public AbstractLifecycle getInstance(TransformationContext context) throws Exception {
        return null;
    }

    @Override
    public void transform(AbstractLifecycle lifecycle) throws Exception {
        // noop
    }
}
