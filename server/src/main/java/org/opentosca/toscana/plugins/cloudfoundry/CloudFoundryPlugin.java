package org.opentosca.toscana.plugins.cloudfoundry;

import java.util.HashSet;
import java.util.Set;

import org.opentosca.toscana.core.plugin.AbstractPlugin;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.properties.Property;

import org.springframework.stereotype.Component;

@Component
public class CloudFoundryPlugin extends AbstractPlugin {

    public CloudFoundryPlugin() {
        super(getPlatformDetails());
    }

    @Override
    public void transform(TransformationContext context) throws Exception {

    }

    private static Platform getPlatformDetails() {
        String platformId = "cloud-foundry";
        String platformName = "CloudFoundry";
        Set<Property> platformProperties = new HashSet<>();
        return new Platform(platformId, platformName, platformProperties);
    }
}
