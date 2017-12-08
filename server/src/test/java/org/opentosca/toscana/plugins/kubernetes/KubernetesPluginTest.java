package org.opentosca.toscana.plugins.kubernetes;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.plugins.lifecycle.ValidationFailureException;
import org.opentosca.toscana.plugins.testdata.TestEffectiveModels;
import org.opentosca.toscana.plugins.util.TestUtil;

import org.junit.Before;
import org.junit.Test;

public class KubernetesPluginTest extends BaseUnitTest {
    private static KubernetesPlugin plugin;

    @Before
    public void setUp() {
        plugin = new KubernetesPlugin();
    }

    @Test(expected = ValidationFailureException.class)
    public void invalidComputeNodeOsType() throws Exception {
        TransformationContext context
            = TestUtil.setUpMockTransformationContext(TestEffectiveModels.getSingleComputeNodeModel());
        plugin.transform(context);
    }
}
