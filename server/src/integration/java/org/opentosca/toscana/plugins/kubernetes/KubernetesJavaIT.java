package org.opentosca.toscana.plugins.kubernetes;

import java.io.File;

import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.core.transformation.properties.NoSuchPropertyException;
import org.opentosca.toscana.core.transformation.properties.PropertyInstance;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.EffectiveModelFactory;

import org.apache.commons.io.FileUtils;

public class KubernetesJavaIT extends KubernetesLampIT {
    public KubernetesJavaIT() throws Exception {
        super();
    }

    @Override
    protected PropertyInstance getInputs(EffectiveModel model) throws NoSuchPropertyException {
        PropertyInstance instance = super.getInputs(model);

        //Set the required model inputs
        instance.set("database_name", "stadb");
        instance.set("database_user", "stadb");
        instance.set("database_port", "3306");
        instance.set("database_password", "TOSCA_rulez");

        return instance;
    }

    @Override
    protected void onFailure(File outputDir, Exception e) {
        //noop
    }

    @Override
    protected void copyArtifacts(File contentDir) throws Exception {
        File inputDir = new File(getClass().getResource("/csars/yaml/valid/task-translator").getFile());
        FileUtils.copyDirectory(inputDir, contentDir);
    }

    @Override
    protected EffectiveModel getModel() {
        return new EffectiveModelFactory().create(TestCsars.VALID_TASKTRANSLATOR_TEMPLATE, logMock());
    }
}
