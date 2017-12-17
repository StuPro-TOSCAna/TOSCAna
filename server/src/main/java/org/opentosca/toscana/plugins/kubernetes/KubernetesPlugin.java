package org.opentosca.toscana.plugins.kubernetes;

import java.util.HashSet;
import java.util.Set;

import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.properties.Property;
import org.opentosca.toscana.plugins.kubernetes.docker.mapper.BaseImageMapper;
import org.opentosca.toscana.plugins.lifecycle.LifecycleAwarePlugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KubernetesPlugin extends LifecycleAwarePlugin<KubernetesLifecycle> {
    private final static Logger logger = LoggerFactory.getLogger(KubernetesPlugin.class);

    private final BaseImageMapper mapper;

    @Autowired
    public KubernetesPlugin(BaseImageMapper mapper) {
        super(getPlatformDetails());
        this.mapper = mapper;
    }

    private static Platform getPlatformDetails() {
        String platformId = "kubernetes";
        String platformName = "Kubernetes";
        Set<Property> platformProperties = new HashSet<>();
        return new Platform(platformId, platformName, platformProperties);
    }
    
    @Override
    protected KubernetesLifecycle getInstance(TransformationContext context) throws Exception {
        return new KubernetesLifecycle(context, mapper);
    }
}
