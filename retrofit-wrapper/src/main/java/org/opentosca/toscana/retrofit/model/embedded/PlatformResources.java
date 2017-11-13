package org.opentosca.toscana.retrofit.model.embedded;

import org.opentosca.toscana.retrofit.model.Platform;
import org.opentosca.toscana.retrofit.model.hal.HALEmbeddedResource;

public class PlatformResources extends HALEmbeddedResource<Platform> {
    @Override
    public String getResourcesName() {
        return "platform";
    }
}
