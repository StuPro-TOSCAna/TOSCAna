package org.opentosca.toscana.retrofit.model.embedded;

import org.opentosca.toscana.retrofit.model.Transformation;
import org.opentosca.toscana.retrofit.model.hal.HALEmbeddedResource;

public class TransformationResources extends HALEmbeddedResource<Transformation> {
    @Override
    public String getResourcesName() {
        return "transformation";
    }
}
