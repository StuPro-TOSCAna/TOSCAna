package org.opentosca.toscana.plugins.kubernetes.model;

import java.io.File;

import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.EffectiveModelFactory;

import org.apache.commons.io.FileUtils;

public class KubernetesExpressIT extends KubernetesLampIT {

    public KubernetesExpressIT() throws Exception {
        super();
    }

    @Override
    protected EffectiveModel getModel() {
        return new EffectiveModelFactory().create(TestCsars.VALID_EXPRESS_TEMPLATE, logMock());
    }

    @Override
    protected void copyArtifacts(File contentDir) throws Exception {
        File inputDir = new File(getClass().getResource("/csars/yaml/valid/express").getFile());
        FileUtils.copyDirectory(inputDir, contentDir);
    }
}
