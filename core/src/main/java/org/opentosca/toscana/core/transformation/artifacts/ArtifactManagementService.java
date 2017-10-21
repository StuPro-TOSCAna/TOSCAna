package org.opentosca.toscana.core.transformation.artifacts;

import org.opentosca.toscana.core.transformation.Transformation;

/**
 * This interface describes a service that will tell spring to serve a file or multiple as a static resource
 */
public interface ArtifactManagementService {
    /**
     * This method takes the target artifact of the transformation and copies it to the "resource directory" to allow
     * Spring to send a URL to the client.
     *
     * @param transformation
     * @return
     */
    String saveToArtifactDirectory(Transformation transformation);
}
