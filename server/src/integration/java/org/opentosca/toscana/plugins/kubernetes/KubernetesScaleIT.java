package org.opentosca.toscana.plugins.kubernetes;

import java.io.File;

import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.EffectiveModelFactory;

public class KubernetesScaleIT extends KubernetesLampIT {
    public KubernetesScaleIT() throws Exception {
        super();
    }

    @Override
    protected EffectiveModel getModel() {
        return new EffectiveModelFactory().create(TestCsars.VALID_SCALED_DOCKER_TEMPLATE, logMock());
    }

    @Override
    protected void copyArtifacts(File contentDir) throws Exception {
        //No files have to be copied, because the model does not have any
        //binary artifacts
    }
}
