package org.opentosca.toscana.core.transformation;

import org.opentosca.toscana.api.exceptions.PlatformNotFoundException;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.transformation.platform.Platform;

public interface TransformationService {

    /**
     Creates a new transformation instance of given csar for given platform.
     Does not start the transformation process.

     @param csar           csar which is going to get transformed
     @param targetPlatform target platform of the new transformation
     @return the new transformation instance
     @throws PlatformNotFoundException if given platform is not supported by any known plugin
     */
    Transformation createTransformation(Csar csar, Platform targetPlatform) throws PlatformNotFoundException;

    /**
     Triggers the start of the transformation of given transformation instance.
     The transformation process itself happens asynchronously.
     That means: method will return before the actual transformation is finished.

     @return true if transformation got queued or started;
     false otherwise (e.g., if it was in state <code>INPUT_REQUIRED</code>)
     */
    boolean startTransformation(Transformation transformation);

    /**
     Aborts the transformation process of given transformation instance.

     @return true if successful, false otherwise
     */
    boolean abortTransformation(Transformation transformation);

    /**
     Deletes given transformation instance from in-memory and any persistence layer.

     @return true if successful, false otherwise
     */
    boolean deleteTransformation(Transformation transformation);
}
