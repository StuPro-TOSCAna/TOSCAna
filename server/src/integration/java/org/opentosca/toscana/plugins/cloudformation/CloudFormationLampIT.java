package org.opentosca.toscana.plugins.cloudformation;

import java.io.File;

import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.properties.PropertyInstance;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.plugins.BaseTransformTest;
import org.opentosca.toscana.plugins.testdata.TestEffectiveModels;

import org.apache.commons.io.FileUtils;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class CloudFormationLampIT extends BaseTransformTest {
    public CloudFormationLampIT() throws Exception {
        super(new CloudFormationPlugin());
    }

    @Override
    protected EffectiveModel getModel() {
        return TestEffectiveModels.getLampModel();
    }

    @Override
    protected void onSuccess(File outputDir) {
        //Do Nothing
    }

    @Override
    protected void onFailure(File outputDir, Exception e) {
        fail();
    }

    @Override
    protected void copyArtifacts(File contentDir) throws Exception {
        File inputDir = new File(getClass().getResource("/csars/yaml/valid/lamp-input").getFile());
        FileUtils.copyDirectory(inputDir, contentDir);
    }

    @Override
    protected PropertyInstance getProperties() {
        return new PropertyInstance(plugin.getPlatform().properties, mock(Transformation.class));
    }
}
