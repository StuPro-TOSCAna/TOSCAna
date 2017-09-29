package org.opentosca.toscana.core.model;

import java.util.Map;

public class Csar {

    /**
     * Stores all scheduled, ongoing or finished transformations of this application.
     * Key is the platform identifier.
     */
    private Map<String, Transformation> transformations;
    private String artifactPath;

    public Csar(String csarArtifactPath) {
        this.artifactPath = csarArtifactPath;
    }

    public void transform(Platform targetPlatform) {
        // TODO
        throw new UnsupportedOperationException();
    }
}

