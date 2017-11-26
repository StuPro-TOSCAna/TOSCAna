package org.opentosca.toscana.plugins.cloudformation;

import java.util.HashSet;
import java.util.Set;

import org.opentosca.toscana.core.plugin.AbstractPlugin;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.properties.Property;

import org.springframework.stereotype.Component;

@Component
public class CloudFormationPlugin extends AbstractPlugin {

    public CloudFormationPlugin() {
        super(getPlatformDetails());
    }

    @Override
    public void transform(TransformationContext context) throws Exception {

    }

    private static Platform getPlatformDetails() {
        String platformId = "cloudformation";
        String platformName = "AWS CloudFormation";
        Set<Property> platformProperties = new HashSet<>();
        return new Platform(platformId, platformName, platformProperties);
    }
}
