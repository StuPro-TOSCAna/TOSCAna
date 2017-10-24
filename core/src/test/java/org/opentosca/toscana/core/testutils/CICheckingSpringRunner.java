package org.opentosca.toscana.core.testutils;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

public class CICheckingSpringRunner extends SpringJUnit4ClassRunner {
    public CICheckingSpringRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
    }

    @Override
    protected boolean isIgnored(FrameworkMethod child) {
        ExcludeContinuousIntegration eci =
            child.getAnnotation(ExcludeContinuousIntegration.class);
        return CIUtils.isCI() && eci != null || super.isIgnored(child);
    }
}
