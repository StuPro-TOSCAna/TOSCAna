package org.opentosca.toscana.core.testutils;

import java.lang.annotation.*;

//Allow retention on class and Method
@Target(value = {ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface TestCategory {
    TestCategories value() default TestCategories.FAST;
}
