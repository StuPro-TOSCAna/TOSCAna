package org.opentosca.toscana.core.testdata.dummyplugins;

import org.opentosca.toscana.core.plugin.TransformationPlugin;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.platform.Platform;

public class DummyPlugin implements TransformationPlugin {

    private final Platform platform;

    public DummyPlugin(Platform platform) {
        this.platform = platform;
    }

    @Override
    public Platform getPlatform() {
        return platform;
    }

    @Override
    public void transform(TransformationContext transformation) throws Exception {

    }
}
