package org.opentosca.toscana.plugins.cloudformation;

import java.io.File;

import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.core.transformation.properties.NoSuchPropertyException;
import org.opentosca.toscana.core.transformation.properties.PropertyInstance;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.EffectiveModelFactory;

import org.apache.commons.io.FileUtils;

public class CloudFormationJavaIT extends CloudFormationIT {

    @Override
    protected EffectiveModel getModel() {
        return new EffectiveModelFactory().create(TestCsars.VALID_TASKTRANSLATOR_TEMPLATE, logMock());
    }

    @Override
    protected void copyArtifacts(File contentDir) throws Exception {
        File inputDir = new File(getClass().getResource("/csars/yaml/valid/task-translator").getFile());
        FileUtils.copyDirectory(inputDir, contentDir);
    }

    @Override
    protected PropertyInstance getInputs(EffectiveModel model) throws NoSuchPropertyException {
        PropertyInstance instance = super.getInputs(model);
        instance.set("database_name", "dbname");
        instance.set("database_port", "3306");
        instance.set("database_password", "abcd1234");
        instance.set("database_user", "root");
        //keep default
        //instance.set("translator_api_key", "");
        //instance.set("translator_update_interval", "");

        return instance;
    }
}
