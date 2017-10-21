package org.opentosca.toscana.core.dummy;

import org.opentosca.toscana.core.plugin.TransformationPlugin;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.platform.Platform;

public class DummyPlugin implements TransformationPlugin {

    private Platform platform;

    public DummyPlugin(Platform platform) {
        this.platform = platform;
    }

    @Override
    public Platform getPlatformDetails() {
        return platform;
    }

    @Override
    public void transform(TransformationContext transformation) throws Exception {

    }
}
