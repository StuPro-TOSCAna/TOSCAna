package org.opentosca.toscana.core.transformation.artifacts;

import java.io.IOException;

import org.opentosca.toscana.core.transformation.Transformation;

/**
 Service that will tell spring to serve a file or multiple as a static resource
 */
public interface ArtifactService {

    /**
     Takes the resulting files of given transformation and zips them.
     */
    TargetArtifact serveArtifact(Transformation transformation) throws IOException;
}
