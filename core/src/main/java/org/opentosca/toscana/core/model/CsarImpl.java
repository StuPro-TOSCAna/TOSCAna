package org.opentosca.toscana.core.model;

import java.util.HashMap;
import java.util.Map;

public class CsarImpl implements Csar{

    /**
     * Stores all scheduled, ongoing or finished transformations of this CSAR.
     * Key is the platform identifier.
     */
    private Map<String, Transformation> transformations = new HashMap<>();
    private String identifier;

    public CsarImpl(String identifier) {
        this.artifactPath = csarArtifactPath;
    }

    public void transform(Platform targetPlatform) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Transformation> getTransformations() {
        return transformations;
    }

    @Override
    public String getName() {
        return null;
    }
}

