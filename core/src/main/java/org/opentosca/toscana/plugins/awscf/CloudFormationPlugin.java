package org.opentosca.toscana.plugins.awscf;

import org.opentosca.toscana.core.plugin.AbstractPlugin;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.springframework.stereotype.Component;

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
    public void transform(TransformationContext context) throws Exception {

    }
}
