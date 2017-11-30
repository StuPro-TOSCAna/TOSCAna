package org.opentosca.toscana.plugins.cloudformation;

import java.io.IOException;

import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.plugins.lifecycle.AbstractLifecycle;

public class CloudFormationLifecycle extends AbstractLifecycle{

    public CloudFormationLifecycle(TransformationContext context) throws IOException {
        super(context);
    }

    @Override
    public boolean checkModel() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void prepare() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transform() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void cleanup() {
        throw new UnsupportedOperationException();
    }
}
