package org.opentosca.toscana.core.csar;

import org.opentosca.toscana.core.transformation.Platform;

public interface CsarService {

    /**
     * Creates a new transformation instance for given platform.
     * Does not start the transformation process.
     * @param targetPlatform target platform of the new transformation
     */
    public void createTransformation(Platform targetPlatform);
}
