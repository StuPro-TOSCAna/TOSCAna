package org.opentosca.toscana.core.testutils;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

public class CICheckingJUnitRunner extends BlockJUnit4ClassRunner {
    public CICheckingJUnitRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected boolean isIgnored(FrameworkMethod child) {
        ExcludeContinuousIntegration eci =
            child.getAnnotation(ExcludeContinuousIntegration.class);
        return CIUtils.isCI() && eci != null || super.isIgnored(child);
    }
}
