package org.opentosca.toscana.core.testdata.dummyplugins;

import org.opentosca.toscana.core.plugin.TOSCAnaPlugin;
import org.opentosca.toscana.core.plugin.lifecycle.TransformationLifecycle;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.platform.Platform;

public class DummyPlugin extends TOSCAnaPlugin {

    public DummyPlugin(Platform platform) {
        super(platform);
    }

    @Override
    public void transform(TransformationContext transformation) throws Exception {

    }

    @Override
    protected TransformationLifecycle getInstance(TransformationContext context) throws Exception {
        return null;
    }
}
