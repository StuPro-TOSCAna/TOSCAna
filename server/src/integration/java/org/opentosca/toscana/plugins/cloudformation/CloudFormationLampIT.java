package org.opentosca.toscana.plugins.cloudformation;

import java.io.File;
import java.util.HashSet;

import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.properties.PropertyInstance;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.plugins.BaseTransformTest;

import org.apache.commons.io.FileUtils;
import org.junit.Assume;

import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationPlugin.AWS_ACCESS_KEY_ID_KEY;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationPlugin.AWS_SECRET_KEY_KEY;

public class CloudFormationLampIT extends BaseTransformTest {
    public CloudFormationLampIT() {
        super(new CloudFormationPlugin());
    }

    @Override
    protected EffectiveModel getModel() {
        return new EffectiveModel(TestCsars.VALID_LAMP_NO_INPUT_TEMPLATE, log);
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
        File inputDir = new File(getClass().getResource("/csars/yaml/valid/lamp-noinput").getFile());
        FileUtils.copyDirectory(inputDir, contentDir);
    }

    @Override
    protected PropertyInstance getProperties() {
        PropertyInstance props =  new PropertyInstance(new HashSet<>(plugin.getPlatform().properties), mock(Transformation.class));
        props.setPropertyValue(AWS_ACCESS_KEY_ID_KEY, System.getenv("AWS_ACCESS_KEY"));
        props.setPropertyValue(AWS_SECRET_KEY_KEY, System.getenv("AWS_SECRET_KEY"));
        return props;
    }

    @Override
    protected void checkAssumptions() {
        PropertyInstance propertyInstance = getProperties();
        Assume.assumeThat(propertyInstance.getPropertyValues().get(AWS_ACCESS_KEY_ID_KEY), not(isEmptyString()));
        Assume.assumeThat(propertyInstance.getPropertyValues().get(AWS_SECRET_KEY_KEY), not(isEmptyString()));
        Assume.assumeNotNull(propertyInstance.getPropertyValues().get(AWS_ACCESS_KEY_ID_KEY));
        Assume.assumeNotNull(propertyInstance.getPropertyValues().get(AWS_SECRET_KEY_KEY));
    }
}
