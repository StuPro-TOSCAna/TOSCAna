package org.opentosca.toscana.plugins.util;

import java.io.IOException;

import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.model.EffectiveModel;

import org.slf4j.LoggerFactory;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestUtil {
    public static TransformationContext setUpMockTransformationContext(EffectiveModel model) throws IOException {
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
