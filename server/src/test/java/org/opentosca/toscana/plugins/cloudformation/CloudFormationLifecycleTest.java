package org.opentosca.toscana.plugins.cloudformation;

import java.io.File;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.EffectiveModelFactory;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CloudFormationLifecycleTest extends BaseUnitTest {

    @Mock
    public TransformationContext context;

    private CloudFormationLifecycle cloudFormationLifecycle;

    @Before
    public void setUp() throws Exception {
        PluginFileAccess access = new PluginFileAccess(new File(""), tmpdir, logMock());
        EffectiveModel effectiveModel = new EffectiveModelFactory().create(TestCsars.VALID_MINIMAL_DOCKER_TEMPLATE, logMock());

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
