package org.opentosca.toscana.plugins.kubernetes;

import java.io.File;

import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.properties.PropertyInstance;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.plugins.BaseTransformTest;
import org.opentosca.toscana.plugins.kubernetes.docker.mapper.MapperTest;

import org.apache.commons.io.FileUtils;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.opentosca.toscana.plugins.kubernetes.KubernetesPlugin.DOCKER_PUSH_TO_REGISTRY_PROPERTY_KEY;
import static org.opentosca.toscana.plugins.kubernetes.KubernetesPlugin.DOCKER_REGISTRY_PASSWORD_PROPERTY_KEY;
import static org.opentosca.toscana.plugins.kubernetes.KubernetesPlugin.DOCKER_REGISTRY_REPOSITORY_PROPERTY_KEY;
import static org.opentosca.toscana.plugins.kubernetes.KubernetesPlugin.DOCKER_REGISTRY_URL_PROPERTY_KEY;
import static org.opentosca.toscana.plugins.kubernetes.KubernetesPlugin.DOCKER_REGISTRY_USERNAME_PROPERTY_KEY;

public class KubernetesLampIT extends BaseTransformTest {

    public KubernetesLampIT() throws Exception {
        super(new KubernetesPlugin(MapperTest.init()));
    }

    @Override
    protected EffectiveModel getModel() {
        return new EffectiveModel(TestCsars.VALID_LAMP_NO_INPUT_TEMPLATE, log);
    }

    @Override
    protected void onSuccess(File outputDir) throws Exception {
        Thread.sleep(10000);
        //Do Nothing
    }

    @Override
    protected void onFailure(File outputDir, Exception e) throws InterruptedException {
        Thread.sleep(10000);
        fail();
    }

    @Override
    protected void copyArtifacts(File contentDir) throws Exception {
        File inputDir = new File(getClass().getResource("/csars/yaml/valid/lamp-noinput").getFile());
        FileUtils.copyDirectory(inputDir, contentDir);
    }

    @Override
    protected PropertyInstance getProperties() {
        PropertyInstance props = new PropertyInstance(plugin.getPlatform().properties, mock(Transformation.class));

        if (System.getenv("DH_USERNAME") != null) {
            //This Transformation is performed by pushing to a registry
            props.setPropertyValue(DOCKER_PUSH_TO_REGISTRY_PROPERTY_KEY, "true");
            props.setPropertyValue(DOCKER_REGISTRY_USERNAME_PROPERTY_KEY, System.getenv("DH_USERNAME"));
            props.setPropertyValue(DOCKER_REGISTRY_PASSWORD_PROPERTY_KEY, System.getenv("DH_PASSWORD"));
            props.setPropertyValue(DOCKER_REGISTRY_URL_PROPERTY_KEY, System.getenv("DH_URL"));
            props.setPropertyValue(DOCKER_REGISTRY_REPOSITORY_PROPERTY_KEY, System.getenv("DH_REPOSITORY"));
        } else {
            //This Transformation is performed by storing the files in Tar archives
            props.setPropertyValue(DOCKER_PUSH_TO_REGISTRY_PROPERTY_KEY, "false");
        }
        return props;
    }
}
