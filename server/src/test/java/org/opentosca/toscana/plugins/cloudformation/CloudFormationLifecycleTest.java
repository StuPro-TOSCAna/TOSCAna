package org.opentosca.toscana.plugins.cloudformation;

import java.io.File;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.properties.PropertyInstance;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.EffectiveModelFactory;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationPlugin.AWS_ACCESS_KEY_ID_KEY;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationPlugin.AWS_REGION_DEFAULT;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationPlugin.AWS_REGION_KEY;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationPlugin.AWS_SECRET_KEY_KEY;

public class CloudFormationLifecycleTest extends BaseUnitTest {

    @Mock
    public TransformationContext context;

    private CloudFormationLifecycle cloudFormationLifecycle;

    @Before
    public void setUp() throws Exception {
        PluginFileAccess accessL = new PluginFileAccess(
            new File(tmpdir, "sourceDir"),
            new File(tmpdir, "targetDir"),
            logMock());
        EffectiveModel effectiveModel = new EffectiveModelFactory().create(TestCsars.VALID_LAMP_NO_INPUT_TEMPLATE, logMock());
        PluginFileAccess access = spy(accessL);
        doNothing().when(access).copy(anyString(), anyString());
        when(context.getPluginFileAccess()).thenReturn(access);
        when(context.getLogger((Class<?>) any(Class.class))).thenReturn(LoggerFactory.getLogger("Dummy Logger"));
        when(context.getModel()).thenReturn(effectiveModel);
        String accessKey = System.getenv("AWS_ACCESS_KEY");
        String secretKey = System.getenv("AWS_SECRET_KEY");
        Assume.assumeNotNull(accessKey);
        Assume.assumeNotNull(secretKey);
        when(context.getInputs()).thenReturn(mock(PropertyInstance.class));
        when(context.getInputs().getOrThrow(AWS_ACCESS_KEY_ID_KEY)).thenReturn(accessKey);
        when(context.getInputs().getOrThrow(AWS_SECRET_KEY_KEY)).thenReturn(secretKey);
        when(context.getInputs().getOrThrow(AWS_REGION_KEY)).thenReturn(AWS_REGION_DEFAULT);
        cloudFormationLifecycle = new CloudFormationLifecycle(context);
    }

    @Test
    public void fullCheck() {
        assertTrue(cloudFormationLifecycle.checkModel());
        cloudFormationLifecycle.prepare();
        cloudFormationLifecycle.transform();
        cloudFormationLifecycle.cleanup();
    }
}
