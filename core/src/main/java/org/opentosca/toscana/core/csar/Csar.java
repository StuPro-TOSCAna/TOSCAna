package org.opentosca.toscana.core.csar;

import org.opentosca.toscana.core.transformation.Transformation;

import java.util.Map;

public interface Csar {

    /**
     * Returns a map of all transformation objects of this csar.
     * This includes scheduled, ongoing and finished transformations.
     * Key of each map entry is the platform identifier of its particular transformation.
     */
    public Map<String,Transformation> getTransformations();

    /**
     * Returns the identifier of the CSAR (which serves as identifier)
     */
    public String getIdentifier();
}
