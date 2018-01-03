package org.opentosca.toscana.plugins.cloudfoundry;

import java.io.File;

import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.properties.PropertyInstance;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.plugins.BaseTransformTest;
import org.opentosca.toscana.plugins.testdata.TestEffectiveModels;

import org.apache.commons.io.FileUtils;

import static org.mockito.Mockito.mock;

/**
 Created by jensmuller on 03.01.18.
 */
public class CloudFoundryLampIT extends BaseTransformTest {
   
    public CloudFoundryLampIT() {
        super(new CloudFoundryPlugin());
    }
    
    @Override
    protected EffectiveModel getModel() throws Exception {
        return TestEffectiveModels.getLampModel();
    }

    @Override
    protected void onSuccess(File outputDir) throws Exception {

    }

    @Override
    protected void onFailure(File outputDir, Exception e) throws Exception {

    }

    @Override
    protected PropertyInstance getProperties() throws Exception {
        return new PropertyInstance(plugin.getPlatform().properties, mock(Transformation.class));
    }

    @Override
    protected void copyArtifacts(File contentDir) throws Exception {
        File inputDir = new File(getClass().getResource("/csars/yaml/valid/lamp-input").getFile());
        FileUtils.copyDirectory(inputDir, contentDir);
    }
}
