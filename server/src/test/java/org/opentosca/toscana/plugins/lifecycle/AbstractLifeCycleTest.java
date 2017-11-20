package org.opentosca.toscana.plugins.lifecycle;

import java.io.File;
import java.io.IOException;

import org.opentosca.toscana.core.BaseJUnitTest;
import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.logging.Log;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbstractLifeCycleTest extends BaseJUnitTest {

    @Before
    public void setUp() throws IOException {
        TransformationContext context = mock(TransformationContext.class);
        PluginFileAccess access = new PluginFileAccess(new File(""), tmpdir, mock(Log.class));
        when(context.getPluginFileAccess()).thenReturn(access);
        when(context.getLogger((Class<?>) any(Class.class))).thenReturn(LoggerFactory.getLogger("Dummy Logger"));
        new TestLifecycle(context);
    }

    @Test
    public void utilFolderCopyTest() {
        File util = new File(tmpdir, "output/scripts/util");
        assertTrue(util.exists());
        assertTrue(util.listFiles().length > 0);
    }

    private class TestLifecycle extends AbstractLifecycle {

        /**
         @param context because the context is always needed this should never be null
         It probably gets called by the <code>getInstance</code> method of the LifecycleAwarePlugin
         */
        public TestLifecycle(TransformationContext context) throws IOException {
            super(context);
        }

        @Override
        public boolean checkModel() {
            return false;
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
}
