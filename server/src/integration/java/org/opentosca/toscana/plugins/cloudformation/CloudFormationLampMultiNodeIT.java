package org.opentosca.toscana.plugins.cloudformation;

import java.io.File;

import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.EffectiveModelFactory;

import org.apache.commons.io.FileUtils;

public class CloudFormationLampMultiNodeIT extends CloudFormationLampIT {

    @Override
    protected EffectiveModel getModel() {
        return new EffectiveModelFactory().create(TestCsars.VALID_LAMP_NO_INPUT_MULTI_COMPUTE_TEMPLATE, logMock());
    }

    @Override
    protected void copyArtifacts(File contentDir) throws Exception {
        File inputDir = new File(getClass().getResource("/csars/yaml/valid/lamp-multinode").getFile());
        FileUtils.copyDirectory(inputDir, contentDir);
    }
}
