package org.opentosca.toscana.plugins.kubernetes;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.properties.InputProperty;
import org.opentosca.toscana.core.transformation.properties.NoSuchPropertyException;
import org.opentosca.toscana.core.transformation.properties.PropertyInstance;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.EffectiveModelFactory;
import org.opentosca.toscana.plugins.BaseTransformTest;
import org.opentosca.toscana.plugins.kubernetes.docker.mapper.MapperTest;

import org.apache.commons.io.FileUtils;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.rules.Timeout;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.opentosca.toscana.plugins.kubernetes.KubernetesPlugin.DOCKER_PUSH_TO_REGISTRY_PROPERTY_KEY;
import static org.opentosca.toscana.plugins.kubernetes.KubernetesPlugin.DOCKER_REGISTRY_PASSWORD_PROPERTY_KEY;
import static org.opentosca.toscana.plugins.kubernetes.KubernetesPlugin.DOCKER_REGISTRY_REPOSITORY_PROPERTY_KEY;
import static org.opentosca.toscana.plugins.kubernetes.KubernetesPlugin.DOCKER_REGISTRY_URL_PROPERTY_KEY;
import static org.opentosca.toscana.plugins.kubernetes.KubernetesPlugin.DOCKER_REGISTRY_USERNAME_PROPERTY_KEY;

public class KubernetesLampIT extends BaseTransformTest {

    @Rule
    public Timeout timeout = Timeout.builder().withTimeout(600, TimeUnit.SECONDS).build();

    public KubernetesLampIT() throws Exception {
        super(new KubernetesPlugin(MapperTest.init()));
    }

    @Override
    protected void checkAssumptions() {
        Assume.assumeNotNull(System.getenv("RUN_K8S_MODEL_TESTS"));
    }

    @Override
    protected EffectiveModel getModel() {
        return new EffectiveModelFactory().create(TestCsars.VALID_LAMP_NO_INPUT_TEMPLATE, logMock());
    }

    @Override
    protected void onSuccess(File outputDir) throws InterruptedException {
        return;
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
    protected PropertyInstance getInputs(EffectiveModel model) throws NoSuchPropertyException {
        Set<InputProperty> prop = new HashSet<>(plugin.getPlatform().properties);
        prop.addAll(model.getInputs().values());
        PropertyInstance instance = new PropertyInstance(prop, mock(Transformation.class));

        if (System.getenv("DH_USERNAME") != null) {
            //This Transformation is performed by pushing to a registry
            instance.set(DOCKER_PUSH_TO_REGISTRY_PROPERTY_KEY, "true");
            instance.set(DOCKER_REGISTRY_USERNAME_PROPERTY_KEY, System.getenv("DH_USERNAME"));
            instance.set(DOCKER_REGISTRY_PASSWORD_PROPERTY_KEY, System.getenv("DH_PASSWORD"));
            instance.set(DOCKER_REGISTRY_URL_PROPERTY_KEY, System.getenv("DH_URL"));
            instance.set(DOCKER_REGISTRY_REPOSITORY_PROPERTY_KEY, System.getenv("DH_REPOSITORY"));
        } else {
            //This Transformation is performed by storing the files in Tar archives
            instance.set(DOCKER_PUSH_TO_REGISTRY_PROPERTY_KEY, "false");
        }
        return instance;
    }
}
