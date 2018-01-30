package org.opentosca.toscana.plugins.kubernetes;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.plugin.lifecycle.ValidationFailureException;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.EffectiveModelFactory;
import org.opentosca.toscana.plugins.kubernetes.docker.mapper.BaseImageMapper;
import org.opentosca.toscana.plugins.kubernetes.docker.mapper.MapperTest;

import org.junit.Before;
import org.junit.Test;

import static org.opentosca.toscana.plugins.util.TestUtil.setUpMockTransformationContext;

public class KubernetesPluginTest extends BaseUnitTest {
    private static KubernetesPlugin plugin;

    private static BaseImageMapper mapper;

    static {
        try {
            mapper = MapperTest.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Before
    public void setUp() {
        plugin = new KubernetesPlugin(mapper);
    }

    @Test(expected = ValidationFailureException.class)
    public void modelCheckTest() throws Exception {
        EffectiveModel singleComputeModel = new EffectiveModelFactory().create(TestCsars.VALID_SINGLE_COMPUTE_WINDOWS_TEMPLATE, logMock());
        TransformationContext context = setUpMockTransformationContext(singleComputeModel);
        plugin.transform(context);
    }
}
