package org.opentosca.toscana.plugins.cloudfoundry;

import java.io.IOException;

import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.plugins.lifecycle.AbstractLifecycle;

import org.slf4j.Logger;

public class CfLifecycle extends AbstractLifecycle {

    private TransformationContext context;
    private final Logger logger = super.logger;

    public CfLifecycle(TransformationContext context) throws IOException{
        super(context);
        this.context=context;
    }

    @Override
    public boolean checkModel() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void prepare() {

    }

    @Override
    public void transform() {
       
    }

    @Override
    public void cleanup() {

    }
}

