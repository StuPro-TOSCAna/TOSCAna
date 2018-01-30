package org.opentosca.toscana.core;

import org.opentosca.toscana.IntegrationTest;

import org.junit.Rule;
import org.junit.experimental.categories.Category;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;

@Category(IntegrationTest.class)
public abstract class BaseIntegrationTest extends BaseUnitTest {

    /**
     Timeout rule
     <p>
     This rule limits the runtime of a test to 5 Minutes
     */
    @Rule
    public final TestRule timeoutRule = new DisableOnDebug(Timeout.seconds(300));
}
