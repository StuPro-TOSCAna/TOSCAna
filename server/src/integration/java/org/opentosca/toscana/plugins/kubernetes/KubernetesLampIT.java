package org.opentosca.toscana.plugins.kubernetes;

import java.io.File;
import java.util.HashSet;

import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.properties.PropertyInstance;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.plugins.BaseTransformTest;
import org.opentosca.toscana.plugins.kubernetes.docker.mapper.MapperTest;
import org.opentosca.toscana.plugins.testdata.KubernetesLampApp;

import org.apache.commons.io.FileUtils;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class KubernetesLampIT extends BaseTransformTest {

    public KubernetesLampIT() throws Exception {
        super(new KubernetesPlugin(MapperTest.init()));
    }

    @Override
    protected EffectiveModel getModel() {
        return new EffectiveModel(KubernetesLampApp.getLampModel(), new HashSet<>());
    }

    @Override
    protected void onSuccess(File outputDir) throws Exception {
        //Do Nothing
    }

    @Override
    protected void onFailure(File outputDir, Exception e) {
        fail();
    }

    @Override
    protected void copyArtifacts(File contentDir) throws Exception {
        File inputDir = new File(getClass().getResource("/kubernetes/csars/lamp").getFile());
        FileUtils.copyDirectory(inputDir, contentDir);
    }

    @Override
    protected PropertyInstance getProperties() {
        return new PropertyInstance(plugin.getPlatform().properties, mock(Transformation.class));
    }
}
