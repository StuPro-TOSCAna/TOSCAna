package org.opentosca.toscana.plugins.cloudformation;

import java.io.File;
import java.util.HashSet;

import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.properties.NoSuchPropertyException;
import org.opentosca.toscana.core.transformation.properties.PropertyInstance;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.EffectiveModelFactory;
import org.opentosca.toscana.plugins.BaseTransformTest;

import org.apache.commons.io.FileUtils;
import org.junit.Assume;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationPlugin.AWS_ACCESS_KEY_ID_KEY;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationPlugin.AWS_KEYPAIR_KEY;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationPlugin.AWS_SECRET_KEY_KEY;

public class CloudFormationExpressIT extends BaseTransformTest {

    private final String accessKey = System.getenv("AWS_ACCESS_KEY");
    private final String secretKey = System.getenv("AWS_SECRET_KEY");

    public CloudFormationExpressIT() {
        super(new CloudFormationPlugin());
    }

    @Override
    protected EffectiveModel getModel() {
        return new EffectiveModelFactory().create(TestCsars.VALID_EXPRESS_TEMPLATE, logMock());
    }

    @Override
    protected void onSuccess(File outputDir) {
        // noop
    }

    @Override
    protected void onFailure(File outputDir, Exception e) {
        fail();
    }

    @Override
    protected void copyArtifacts(File contentDir) throws Exception {
        File inputDir = new File(getClass().getResource("/csars/yaml/valid/express").getFile());
        FileUtils.copyDirectory(inputDir, contentDir);
    }

    @Override
    protected PropertyInstance getInputs(EffectiveModel model) throws NoSuchPropertyException {
        PropertyInstance props = new PropertyInstance(new HashSet<>(plugin.getPlatform().properties), mock
            (Transformation.class));
        props.set(AWS_ACCESS_KEY_ID_KEY, accessKey);
        props.set(AWS_SECRET_KEY_KEY, secretKey);
        props.set(AWS_KEYPAIR_KEY, "true");
        return props;
    }

    @Override
    protected void checkAssumptions() {
        Assume.assumeNotNull(accessKey);
        Assume.assumeNotNull(secretKey);
    }
}
