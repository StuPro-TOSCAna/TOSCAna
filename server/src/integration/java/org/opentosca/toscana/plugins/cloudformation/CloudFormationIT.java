package org.opentosca.toscana.plugins.cloudformation;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.properties.InputProperty;
import org.opentosca.toscana.core.transformation.properties.NoSuchPropertyException;
import org.opentosca.toscana.core.transformation.properties.PropertyInstance;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.plugins.BaseTransformTest;

import org.junit.Assume;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationPlugin.AWS_ACCESS_KEY_ID_KEY;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationPlugin.AWS_KEYPAIR_KEY;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationPlugin.AWS_SECRET_KEY_KEY;

public abstract class CloudFormationIT extends BaseTransformTest {

    private String accessKey;
    private String secretKey;

    public CloudFormationIT() {
        super(new CloudFormationPlugin());
    }

    @Override
    protected void onSuccess(File outputDir) {
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
        accessKey = System.getenv("AWS_ACCESS_KEY");
        secretKey = System.getenv("AWS_SECRET_KEY");
        Assume.assumeNotNull(accessKey);
        Assume.assumeNotNull(secretKey);
    }
}
