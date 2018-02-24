package org.opentosca.toscana.plugins.cloudfoundry;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.properties.InputProperty;
import org.opentosca.toscana.core.transformation.properties.NoSuchPropertyException;
import org.opentosca.toscana.core.transformation.properties.PropertyInstance;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.EffectiveModelFactory;
import org.opentosca.toscana.plugins.BaseTransformTest;

import org.apache.commons.io.FileUtils;

import static org.junit.Assert.fail;
import static org.junit.Assume.assumeNotNull;
import static org.mockito.Mockito.mock;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryPlugin.CF_PROPERTY_KEY_API;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryPlugin.CF_PROPERTY_KEY_ORGANIZATION;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryPlugin.CF_PROPERTY_KEY_PASSWORD;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryPlugin.CF_PROPERTY_KEY_SPACE;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryPlugin.CF_PROPERTY_KEY_USERNAME;
import static org.opentosca.toscana.plugins.cloudfoundry.ServiceTest.CF_ENVIRONMENT_HOST;
import static org.opentosca.toscana.plugins.cloudfoundry.ServiceTest.CF_ENVIRONMENT_ORGA;
import static org.opentosca.toscana.plugins.cloudfoundry.ServiceTest.CF_ENVIRONMENT_PW;
import static org.opentosca.toscana.plugins.cloudfoundry.ServiceTest.CF_ENVIRONMENT_SPACE;
import static org.opentosca.toscana.plugins.cloudfoundry.ServiceTest.CF_ENVIRONMENT_USER;

public class CloudFoundryLampMultiNode extends BaseTransformTest {

    private final String envUser = System.getenv(CF_ENVIRONMENT_USER);
    private final String envPw = System.getenv(CF_ENVIRONMENT_PW);
    private final String envHost = System.getenv(CF_ENVIRONMENT_HOST);
    private final String envOrga = System.getenv(CF_ENVIRONMENT_ORGA);
    private final String envSpace = System.getenv(CF_ENVIRONMENT_SPACE);

    public CloudFoundryLampMultiNode() {
        super(new CloudFoundryPlugin());
    }

    @Override
    protected EffectiveModel getModel() {
        return new EffectiveModelFactory().create(TestCsars.VALID_LAMP_NO_INPUT_MULTI_COMPUTE_TEMPLATE, logMock());
    }

    @Override
    protected void onSuccess(File outputDir) throws InterruptedException {
        //System.out.println("You can stop me now");
        //Thread.sleep(5000);
    }

    @Override
    protected void onFailure(File outputDir, Exception e) {
        fail();
    }

    protected PropertyInstance getInputs(EffectiveModel model) throws NoSuchPropertyException {
        Set<InputProperty> prop = new HashSet<>(plugin.getPlatform().properties);
        prop.addAll(model.getInputs().values());
        PropertyInstance props = new PropertyInstance(prop, mock(Transformation.class));
        props.set(CF_PROPERTY_KEY_USERNAME, envUser);
        props.set(CF_PROPERTY_KEY_PASSWORD, envPw);
        props.set(CF_PROPERTY_KEY_API, envHost);
        props.set(CF_PROPERTY_KEY_ORGANIZATION, envOrga);
        props.set(CF_PROPERTY_KEY_SPACE, envSpace);

        return props;
    }

    @Override
    protected void copyArtifacts(File contentDir) throws Exception {
        File inputDir = new File(getClass().getResource("/csars/yaml/valid/lamp-multinode").getFile());
        FileUtils.copyDirectory(inputDir, contentDir);
    }

    @Override
    protected void checkAssumptions() {
        assumeNotNull(envUser, envPw, envHost, envOrga, envSpace);
    }
}


