package org.opentosca.toscana.core.csar;

import org.opentosca.toscana.core.transformation.Platform;
import org.opentosca.toscana.core.transformation.Transformation;

import java.util.HashMap;
import java.util.Map;

class CsarImpl implements Csar{

    /**
     * Stores all scheduled, ongoing or finished transformations of this CSAR.
     * Key is the platform identifier.
     */
    private Map<String, Transformation> transformations = new HashMap<>();
    private String identifier;

    public CsarImpl(String identifier) {
        this.identifier = identifier;
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
    public String getIdentifier() {
        return identifier;
    }

}

