package org.opentosca.toscana.plugins.kubernetes;

import org.opentosca.toscana.core.plugin.AbstractPlugin;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.properties.Property;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class KubernetesPlugin extends AbstractPlugin {
    @Override
    public String getName() {
        return "Kubernetes";
    }

    @Override
    public String getIdentifier() {
        return "kubernetes";
    }

    @Override
    public HashSet<Property> getPluginSpecificProperties() {
        return new HashSet<>();
    }
    
}
