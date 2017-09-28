package org.opentosca.toscana.core.data;

import java.util.Map;

public class Csar {

    /**
     * Stores all scheduled, ongoing or finished transformations of this application.
     * Key is the platform identifier.
     */
    private Map<String, Transformation> transformations;
    private CsarArtifact artifact;

    public Csar(CsarArtifact artifact){
        this.artifact = artifact;
    }

    public void transform(Platform targetPlatform){
        // TODO
        throw new UnsupportedOperationException();
    }
}

