package org.opentosca.toscana.core.testutils;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

public class CategoryAwareJUnitRunner extends BlockJUnit4ClassRunner {
    
    private boolean skipAll = false;
    
    public CategoryAwareJUnitRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
        TestCategory mode = clazz.getAnnotation(TestCategory.class);
        if(mode != null) {
            skipAll = !TestCategories.getCurrentTestMode().shouldBeExecuted(mode.value());
        } else {
            skipAll = !TestCategories.getCurrentTestMode().shouldBeExecuted(TestCategories.FAST);
        }
    }
    
    @Override
    protected boolean isIgnored(FrameworkMethod child) {
        if(skipAll) {
            return true;
        }
        TestCategory eci =
            child.getAnnotation(TestCategory.class);
        return (eci != null && !TestCategories.getCurrentTestMode().shouldBeExecuted(eci.value())) || super.isIgnored(child);
    }
}
