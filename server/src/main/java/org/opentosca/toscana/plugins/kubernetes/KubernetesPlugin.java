package org.opentosca.toscana.plugins.kubernetes;

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
public class KubernetesPlugin extends LifecycleAwarePlugin<KubernetesLifecycle> {
    private final static Logger logger = LoggerFactory.getLogger(KubernetesPlugin.class);

    public KubernetesPlugin() {
        super(getPlatformDetails());
    }

    private static Platform getPlatformDetails() {
        String platformId = "kubernetes";
        String platformName = "Kubernetes";
        Set<Property> platformProperties = new HashSet<>();
        return new Platform(platformId, platformName, platformProperties);
    }

    @Override
    public void transform(TransformationContext context) throws Exception {
        logger.info("Started transformation to kubernetes artifact.");
    }

    @Override
    protected Set<Class<?>> getSupportedNodeTypes() {
        return null;
    }

    @Override
    protected KubernetesLifecycle getInstance(TransformationContext context) throws Exception {
        return null;
    }
}
