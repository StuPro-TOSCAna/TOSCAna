package org.opentosca.toscana.core.transformation.artifacts;

import org.opentosca.toscana.core.transformation.Transformation;

import java.io.File;
import java.io.IOException;

/**
 * Service that will tell spring to serve a file or multiple as a static resource
 */
public interface ArtifactService {

    /**
     * Takes the target artifact of given transformation and copies it to the "resource directory" in order to allow
     * Spring to serve its URL to the client.
     */
    TargetArtifact serveArtifact(Transformation transformation) throws IOException;

    /**
     * @return the general target artifact dir
     */
    File getArtifactDir();
}
