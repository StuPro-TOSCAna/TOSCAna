package org.opentosca.toscana.plugins.cloudfoundry;

import java.io.IOException;

import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.plugins.lifecycle.AbstractLifecycle;

public class CloudFoundryLifecycle extends AbstractLifecycle {

    private final TransformationContext context;

    public CloudFoundryLifecycle(TransformationContext context) throws IOException {
        super(context);
        this.context = context;
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

