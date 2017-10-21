package org.opentosca.toscana.plugins.cloudfoundry;

import org.opentosca.toscana.core.plugin.AbstractPlugin;
import org.opentosca.toscana.core.transformation.properties.Property;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class CloudFoundryPlugin extends AbstractPlugin {
    @Override
    public String getName() {
        return "CloudFoundry";
    }

    @Override
    public String getIdentifier() {
        return "cloud-foundry";
    }

    @Override
    public HashSet<Property> getPluginSpecificProperties() {
        return new HashSet<>();
    }
}
