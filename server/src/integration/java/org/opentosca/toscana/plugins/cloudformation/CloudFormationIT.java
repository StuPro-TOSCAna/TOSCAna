package org.opentosca.toscana.plugins.cloudformation;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.properties.InputProperty;
import org.opentosca.toscana.core.transformation.properties.NoSuchPropertyException;
import org.opentosca.toscana.core.transformation.properties.PropertyInstance;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.plugins.BaseTransformTest;

import org.apache.commons.io.FileUtils;
import org.junit.Assume;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationPlugin.AWS_ACCESS_KEY_ID_KEY;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationPlugin.AWS_KEYPAIR_KEY;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationPlugin.AWS_SECRET_KEY_KEY;

public abstract class CloudFormationIT extends BaseTransformTest {

    private final String accessKey = System.getenv("AWS_ACCESS_KEY");
    private final String secretKey = System.getenv("AWS_SECRET_KEY");

    public CloudFormationIT() {
        super(new CloudFormationPlugin());
    }

    @Override
    protected void onSuccess(File outputDir) throws IOException {
        FileUtils.copyDirectory(outputDir, new File("/home/manuel/tmp2/"));
        System.out.println("copied");
    }

    @Override
    protected void onFailure(File outputDir, Exception e) {
        fail();
    }

    @Override
    protected PropertyInstance getInputs(EffectiveModel model) throws NoSuchPropertyException {
        Set<InputProperty> prop = new HashSet<>(plugin.getPlatform().properties);
        prop.addAll(model.getInputs().values());
        PropertyInstance props = new PropertyInstance(prop, mock(Transformation.class));
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
