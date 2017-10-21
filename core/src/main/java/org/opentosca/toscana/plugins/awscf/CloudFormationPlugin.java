package org.opentosca.toscana.plugins.awscf;

import org.opentosca.toscana.core.plugin.AbstractPlugin;
import org.opentosca.toscana.core.transformation.properties.Property;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class CloudFormationPlugin extends AbstractPlugin {
    @Override
    public String getName() {
        return "Amazon Cloud Formation";
    }

    @Override
    public String getIdentifier() {
        return "cloud-formation";
    }

    @Override
    public HashSet<Property> getPluginSpecificProperties() {
        return new HashSet<>();
    }
}
