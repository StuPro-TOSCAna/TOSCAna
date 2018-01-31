package org.opentosca.toscana.plugins.kubernetes;

import java.io.File;

import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.EffectiveModelFactory;

import org.apache.commons.io.FileUtils;

public class KubernetesLampMultiNodeIT extends KubernetesLampIT {
    
    public KubernetesLampMultiNodeIT() throws Exception {
        super();
    }

    @Override
    protected EffectiveModel getModel() {
        return new EffectiveModelFactory().create(TestCsars.VALID_LAMP_NO_INPUT_MULTI_COMPUTE_TEMPLATE, logMock());
    }

    @Override
    protected void copyArtifacts(File contentDir) throws Exception {
        File inputDir = new File(getClass().getResource(TestCsars.CSAR_LAMP_MULTINODE_NOINPUT_PATH).getFile());
        FileUtils.copyDirectory(inputDir, contentDir);
    }
}
