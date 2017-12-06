package org.opentosca.toscana.plugins.kubernetes;

import java.io.IOException;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.plugins.lifecycle.ValidationFailureException;
import org.opentosca.toscana.plugins.testdata.TestEffectiveModels;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class KubernetesPluginTest extends BaseUnitTest {
    private static KubernetesPlugin plugin;

    @Before
    public void setUp() {
        plugin = new KubernetesPlugin();
    }

    @Test(expected = ValidationFailureException.class)
    public void modelCheckTest() throws Exception {
        TransformationContext context = setUpMockTransformationContext(TestEffectiveModels.getSingleComputeNodeModel());
        plugin.transform(context);
    }

    private TransformationContext setUpMockTransformationContext(EffectiveModel model) throws IOException {
        TransformationContext context = mock(TransformationContext.class);
        PluginFileAccess pluginFileAccess = mock(PluginFileAccess.class);
        when(context.getPluginFileAccess()).thenReturn(pluginFileAccess);
        when(context.getModel()).thenReturn(model);
        when(context.getLogger((Class<?>) any(Class.class))).thenReturn(LoggerFactory.getLogger("Dummy Logger"));
        PluginFileAccess.BufferedLineWriter mock = mock(PluginFileAccess.BufferedLineWriter.class);
        when(pluginFileAccess.access(any(String.class))).thenReturn(mock);
        when(mock.append(any(String.class))).thenReturn(mock);
        return context;
    }
}
