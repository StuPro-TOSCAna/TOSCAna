package org.opentosca.toscana.core.plugin;

import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.platform.Platform;

public interface TransformationPlugin {
    Platform getPlatformDetails();

    void transform(TransformationContext context) throws Exception;
}
