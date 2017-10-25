package org.opentosca.toscana.plugins.cloudfoundry;

import org.opentosca.toscana.core.plugin.AbstractPlugin;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.springframework.stereotype.Component;

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
    public void transform(TransformationContext context) throws Exception {

    }
}
