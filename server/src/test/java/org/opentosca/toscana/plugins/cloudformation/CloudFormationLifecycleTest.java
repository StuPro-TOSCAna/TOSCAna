package org.opentosca.toscana.plugins.cloudformation;

import java.io.File;
import java.util.HashSet;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.model.EffectiveModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CloudFormationLifecycleTest extends BaseUnitTest {

    private static final Logger logger = LoggerFactory.getLogger(CloudFormationLifecycle.class);

    @Mock
    public TransformationContext context;

    public CloudFormationLifecycle cloudFormationLifecycle;

    @Before
    public void setUp() throws Exception {
        PluginFileAccess access = new PluginFileAccess(new File(""), tmpdir, mock(Log.class));
        EffectiveModel effectiveModel = new EffectiveModel(new HashSet<>());
        when(context.getPluginFileAccess()).thenReturn(access);
        when(context.getLogger((Class<?>) any(Class.class))).thenReturn(LoggerFactory.getLogger("Dummy Logger"));
        when(context.getModel()).thenReturn(effectiveModel);
        cloudFormationLifecycle = new CloudFormationLifecycle(context);
    }

    @Test
    public void fullCheck() {
        assertTrue(cloudFormationLifecycle.checkModel());
        cloudFormationLifecycle.prepare();
        cloudFormationLifecycle.transform();
        cloudFormationLifecycle.prepare();
    }
}
