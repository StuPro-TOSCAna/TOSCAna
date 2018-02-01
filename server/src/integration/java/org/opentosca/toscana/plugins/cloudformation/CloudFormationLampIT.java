package org.opentosca.toscana.plugins.cloudformation;

import java.io.File;
import java.util.HashSet;

import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.properties.PropertyInstance;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.EffectiveModelFactory;
import org.opentosca.toscana.plugins.BaseTransformTest;

import org.apache.commons.io.FileUtils;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class CloudFormationLampIT extends BaseTransformTest {
    public CloudFormationLampIT() {
        super(new CloudFormationPlugin());
    }

    @Override
    protected EffectiveModel getModel() {
        return new EffectiveModelFactory().create(TestCsars.VALID_LAMP_NO_INPUT_TEMPLATE, logMock());
    }

    @Override
    protected void onSuccess(File outputDir) {
        return;
    }

    @Override
    protected void onFailure(File outputDir, Exception e) {
        fail();
    }

    @Override
    protected void copyArtifacts(File contentDir) throws Exception {
        File inputDir = new File(getClass().getResource("/csars/yaml/valid/lamp-noinput").getFile());
        FileUtils.copyDirectory(inputDir, contentDir);
    }

    @Override
    protected PropertyInstance getProperties() {
        return new PropertyInstance(new HashSet<>(plugin.getPlatform().properties), mock(Transformation.class));
    }
}
