package org.opentosca.toscana.retrofit.model.embedded;

import org.opentosca.toscana.retrofit.model.Csar;
import org.opentosca.toscana.retrofit.model.hal.HALEmbeddedResource;

public class CsarResources extends HALEmbeddedResource<Csar> {

    @Override
    public String getResourcesName() {
        return "csar";
    }
}
