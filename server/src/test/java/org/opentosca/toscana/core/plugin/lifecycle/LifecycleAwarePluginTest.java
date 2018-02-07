package org.opentosca.toscana.core.plugin.lifecycle;

import java.io.IOException;
import java.util.HashSet;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarImpl;
import org.opentosca.toscana.core.testdata.TestPlugins;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.TransformationImpl;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.properties.NoSuchPropertyException;
import org.opentosca.toscana.model.EffectiveModel;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LifecycleAwarePluginTest extends BaseUnitTest {

    private static final Logger logger = LoggerFactory.getLogger(LifecycleAwarePluginTest.class);

    private TransformationContext context;

    private TestTransformationLifecycle lifecycle;

    private boolean checkEnvironment = true;
    private boolean checkModel = true;

    private LifecycleTestPlugin plugin;
    private Platform platform;

    @Before
    public void setUp() throws Exception {
        Csar csar = new CsarImpl(tmpdir, "csarId", logMock());
        Transformation t = new TransformationImpl(csar, TestPlugins.PLATFORM1, logMock(), mock(EffectiveModel.class));
        context = spy(new TransformationContext(t, tmpdir));
        doReturn(false).when(context).performDeployment();
        lifecycle = spy(new TestTransformationLifecycle(context));
        checkModel = true;
        plugin = new LifecycleTestPlugin(TestPlugins.PLATFORM1);
    }

    @Test
    public void testDeploymentEnabled() throws Exception {
        when(context.performDeployment()).thenReturn(true);
        plugin = new LifecycleTestPlugin(new Platform("test", "test", true, new HashSet<>()));
        lifecycle = spy(new TestTransformationLifecycle(context));
        checkModel = true;
        plugin.transform(lifecycle);
        verify(lifecycle, times(1)).deploy();
    }

    @Test
    public void testDeploymentDisabled() throws Exception {
        when(context.performDeployment()).thenReturn(false);
        plugin = new LifecycleTestPlugin(TestPlugins.PLATFORM4);
        plugin.transform(lifecycle);
        verify(lifecycle, times(0)).deploy();
    }

    @Test
    public void checkEnvFailure() throws Exception {
        checkEnvironment = false;
        plugin = new LifecycleTestPlugin(TestPlugins.PLATFORM4);
        try {
            plugin.transform(lifecycle);
        } catch (ValidationFailureException e) {
            logger.info("Thrown exception: ", e);
            verify(lifecycle, never()).checkModel();
            return;
        }
        fail();
    }

    @Test
    public void checkModelFailure() throws Exception {
        checkModel = false;
        try {
            plugin.transform(lifecycle);
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
        plugin.transform(lifecycle);
        verify(lifecycle).cleanup();
    }

    private class LifecycleTestPlugin extends ToscanaPlugin {

        //Not using mockito because it somehow did not work as expected using spy()
        public LifecycleTestPlugin(Platform platform) {
            super(platform);
        }

        @Override
        public AbstractLifecycle getInstance(TransformationContext context) {
            return lifecycle;
        }
    }

    private class TestTransformationLifecycle extends AbstractLifecycle {
        
        public TestTransformationLifecycle(TransformationContext context) throws IOException {
            super(context);
        }

        @Override
        public boolean checkEnvironment() {
            System.out.println("Check Env");
            return checkEnvironment;
        }

        @Override
        public boolean checkModel() {
            System.out.println("Check Model");
            return checkModel;
        }

        @Override
        public void prepare() throws NoSuchPropertyException {
            System.out.println("Prepare");
        }

        @Override
        public void transform() {
            System.out.println("Transform");
        }

        @Override
        public void cleanup() {
            System.out.println("Cleanup");
        }

        @Override
        public void deploy() {
            System.out.println("Deploy");
        }
    }
}
