package org.opentosca.toscana.core.testutils;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

public class CategoryAwareSpringRunner extends SpringJUnit4ClassRunner {

    private boolean skipAll = false;

    public CategoryAwareSpringRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
        TestCategory mode = clazz.getAnnotation(TestCategory.class);
        if (mode != null) {
            skipAll = !TestCategories.getCurrentTestMode().shouldBeExecuted(mode.value());
        } else {
            skipAll = !TestCategories.getCurrentTestMode().shouldBeExecuted(TestCategories.FAST);
        }
    }

    @Override
    protected boolean isTestMethodIgnored(FrameworkMethod child) {
        if (skipAll) {
            return true;
        }
        TestCategory eci =
            child.getAnnotation(TestCategory.class);
        return (eci != null && !TestCategories.getCurrentTestMode().shouldBeExecuted(eci.value()))
            || super.isTestMethodIgnored(child);

    }

    @Override
    protected boolean isIgnored(FrameworkMethod child) {
        if (skipAll) {
            return true;
        }
        TestCategory eci =
            child.getAnnotation(TestCategory.class);
        return (eci != null && !TestCategories.getCurrentTestMode().shouldBeExecuted(eci.value())) || super.isIgnored(child);
    }
}
