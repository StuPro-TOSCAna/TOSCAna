package org.opentosca.toscana.core.transformation.artifacts;

import java.io.IOException;

import org.opentosca.toscana.core.transformation.Transformation;

/**
 * Service that will tell spring to serve a file or multiple as a static resource
 */
public interface ArtifactManagementService {

    /**
     * Takes the target artifact of the transformation and copies it to the "resource directory" to allow Spring to
     * serve its URL to the client.
     */
    String serveArtifact(Transformation transformation) throws IOException;
}
