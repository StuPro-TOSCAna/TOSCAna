package org.opentosca.toscana.plugins.cloudfoundry;

import java.io.File;

import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.core.transformation.properties.NoSuchPropertyException;
import org.opentosca.toscana.core.transformation.properties.PropertyInstance;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.EffectiveModelFactory;

import org.apache.commons.io.FileUtils;

import static org.junit.Assert.fail;

public class CloudFoundryJavaIT extends CloudFoundryLampIT {
    public CloudFoundryJavaIT() throws Exception {
        super();
    }

    @Override
    protected EffectiveModel getModel() throws Exception {
        return new EffectiveModelFactory().create(TestCsars.VALID_TASKTRANSLATOR_TEMPLATE, logMock());
    }

    @Override
    protected PropertyInstance getInputs(EffectiveModel model) throws NoSuchPropertyException {
        PropertyInstance props = super.getInputs(model);
        props.set("database_name", "name");
        props.set("database_user", "user");
        props.set("database_port", "3333");
        props.set("database_password", "secrets");

        return props;
    }

    @Override
    protected void onFailure(File outputDir, Exception e) throws Exception {
        fail();
    }

    @Override
    protected void copyArtifacts(File contentDir) throws Exception {
        File inputDir = new File(getClass().getResource("/csars/yaml/valid/task-translator").getFile());
        FileUtils.copyDirectory(inputDir, contentDir);
    }
}
