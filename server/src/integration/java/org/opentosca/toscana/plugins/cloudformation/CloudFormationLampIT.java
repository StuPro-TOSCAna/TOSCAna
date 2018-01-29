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

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationPlugin.AWS_ACCESS_KEY_ID_KEY;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationPlugin.AWS_SECRET_KEY_KEY;

public class CloudFormationLampIT extends BaseTransformTest {

    private final String accessKey = System.getenv("AWS_ACCESS_KEY");
    private final String secretKey = System.getenv("AWS_SECRET_KEY");

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
        PropertyInstance props = new PropertyInstance(new HashSet<>(plugin.getPlatform().properties), mock
            (Transformation.class));
        props.setPropertyValue(AWS_ACCESS_KEY_ID_KEY, accessKey);
        props.setPropertyValue(AWS_SECRET_KEY_KEY, secretKey);
        return props;
    }

    @Override
    protected void checkAssumptions() {
        Assume.assumeNotNull(accessKey);
        Assume.assumeNotNull(secretKey);
    }
}
