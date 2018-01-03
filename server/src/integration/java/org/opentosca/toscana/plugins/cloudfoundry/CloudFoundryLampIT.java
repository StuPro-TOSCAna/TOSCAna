package org.opentosca.toscana.plugins.cloudfoundry;

import java.io.File;

import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.properties.PropertyInstance;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.plugins.BaseTransformTest;
import org.opentosca.toscana.plugins.testdata.TestEffectiveModels;

import org.apache.commons.io.FileUtils;

import static org.junit.Assume.assumeNotNull;
import static org.mockito.Mockito.mock;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryPlugin.CF_PROPERTY_KEY_API;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryPlugin.CF_PROPERTY_KEY_ORGANIZATION;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryPlugin.CF_PROPERTY_KEY_PASSWORD;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryPlugin.CF_PROPERTY_KEY_SPACE;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryPlugin.CF_PROPERTY_KEY_USERNAME;

/**
 Created by jensmuller on 03.01.18.
 */
public class CloudFoundryLampIT extends BaseTransformTest {

    public CloudFoundryLampIT() {
        super(new CloudFoundryPlugin());
    }

    @Override
    protected EffectiveModel getModel() throws Exception {
        return TestEffectiveModels.getLampModel();
    }

    @Override
    protected void onSuccess(File outputDir) throws Exception {
        System.out.println("You can stop me now");
        Thread.sleep(5000);
    }

    @Override
    protected void onFailure(File outputDir, Exception e) throws Exception {

    }

    @Override
    protected PropertyInstance getProperties() throws Exception {
        String envUser = System.getenv("TEST_CF_USER");
        String envPw = System.getenv("TEST_CF_PW");
        String envHost = System.getenv("TEST_CF_HOST");
        String envOrga = System.getenv("TEST_CF_ORGA");
        String envSpace = System.getenv("TEST_CF_SPACE");

        assumeNotNull(envUser, envHost, envOrga, envPw, envSpace);

        PropertyInstance props = new PropertyInstance(plugin.getPlatform().properties, mock(Transformation.class));
        props.setPropertyValue(CF_PROPERTY_KEY_USERNAME, envUser);
        props.setPropertyValue(CF_PROPERTY_KEY_PASSWORD, envPw);
        props.setPropertyValue(CF_PROPERTY_KEY_API, envHost);
        props.setPropertyValue(CF_PROPERTY_KEY_ORGANIZATION, envOrga);
        props.setPropertyValue(CF_PROPERTY_KEY_SPACE, envSpace);

        return props;
    }

    @Override
    protected void copyArtifacts(File contentDir) throws Exception {
        File inputDir = new File(getClass().getResource("/csars/yaml/valid/lamp-input").getFile());
        FileUtils.copyDirectory(inputDir, contentDir);
    }
}
