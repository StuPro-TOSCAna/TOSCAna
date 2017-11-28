package org.opentosca.toscana.plugins.cloudfoundry;

import java.util.HashSet;
import java.util.Set;

import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.properties.Property;
import org.opentosca.toscana.plugins.lifecycle.LifecycleAwarePlugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CloudFoundryPlugin extends LifecycleAwarePlugin<CfLifecycle> {
    private final static Logger logger = LoggerFactory.getLogger(CloudFoundryPlugin.class);

    public CloudFoundryPlugin() {
        super(getPlatformDetails());
    }


    @Override
    protected CfLifecycle getInstance(TransformationContext context) throws Exception {
        return new CfLifecycle(context);
    }

    private static Platform getPlatformDetails() {
        String platformId = "cloud-foundry";
        String platformName = "CloudFoundry";
        Set<Property> platformProperties = new HashSet<>();
        return new Platform(platformId, platformName, platformProperties);
    }
}
