package org.opentosca.toscana.core.csar;

import org.opentosca.toscana.core.transformation.Platform;
import org.opentosca.toscana.core.transformation.TransformationImpl;

import java.util.HashMap;
import java.util.Map;

class CsarImpl implements Csar{

    /**
     * Stores all scheduled, ongoing or finished transformations of this CSAR.
     * Key is the platform identifier.
     */
    private Map<String, TransformationImpl> transformations = new HashMap<>();
    private String identifier;

    public CsarImpl(String identifier) {
        this.artifactPath = csarArtifactPath;
    }

    public void transform(Platform targetPlatform) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, TransformationImpl> getTransformations() {
        return transformations;
    }

    @Override
    public String getName() {
        return null;
    }
}

