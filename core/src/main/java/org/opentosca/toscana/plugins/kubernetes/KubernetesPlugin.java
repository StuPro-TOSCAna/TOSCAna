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

    @Override
    public void transform(Transformation transformation) throws Exception {
        Logger logger = transformation.getTransformationLogger(getClass());
        for (int i = 0; i < 150; i++) {
            logger.info("Execution Round {}, Delay {}", i, i * 10);
            Thread.sleep(10 * i);
            if (i == 100) {
                logger.info("Test error", new RuntimeException(i + ""));
            }
        }
    }
}
