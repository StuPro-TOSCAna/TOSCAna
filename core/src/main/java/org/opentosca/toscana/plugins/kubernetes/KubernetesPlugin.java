package org.opentosca.toscana.plugins.kubernetes;

import org.opentosca.toscana.core.plugin.AbstractPlugin;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.springframework.stereotype.Component;

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
    public void transform(TransformationContext context) throws Exception {

    }
}
