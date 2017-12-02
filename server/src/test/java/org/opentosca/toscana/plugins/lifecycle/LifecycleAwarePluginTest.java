package org.opentosca.toscana.plugins.lifecycle;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.testdata.TestPlugins;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.platform.Platform;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LifecycleAwarePluginTest extends BaseUnitTest {

    private static final Logger logger = LoggerFactory.getLogger(LifecycleAwarePluginTest.class);

    @Mock
    public TransformationContext context;

    @Mock
    public TransformationLifecycle lifecycle;

    public LifecycleTestPlugin plugin;

    @Before
    public void setUp() throws Exception {
        when(context.getLogger((Class<?>) any(Class.class))).thenReturn(LoggerFactory.getLogger("Dummy Logger"));
        plugin = new LifecycleTestPlugin(TestPlugins.PLATFORM1);
    }

    @Test
    public void checkEnvFailure() throws Exception {
        plugin.envCheckReturnValue = false;
        try {
            plugin.transform(context);
        } catch (ValidationFailureException e) {
            logger.info("Thrown exception: ", e);
            verify(lifecycle, never()).checkModel();
            return;
        }
        fail();
    }

    @Test
    public void checkModelFailure() throws Exception {
        try {
            plugin.transform(context);
        } catch (ValidationFailureException e) {
            logger.info("Thrown exception: ", e);
            verify(lifecycle, never()).prepare();
            return;
        }
        fail();
    }

    @Test
    public void successfulExec() throws Exception {
        when(lifecycle.checkModel()).thenReturn(true);
        plugin.transform(context);
        verify(lifecycle).cleanup();
    }

    private class LifecycleTestPlugin extends LifecycleAwarePlugin {

        //Not using mockito because it somehow did not work as expected using spy()
        private boolean envCheckReturnValue = true;

        public LifecycleTestPlugin(Platform platform) {
            super(platform);
        }

        @Override
        protected boolean checkEnvironment() {
            return envCheckReturnValue;
        }

        @Override
        protected TransformationLifecycle getInstance(TransformationContext context) {
            return lifecycle;
        }
    }
}
