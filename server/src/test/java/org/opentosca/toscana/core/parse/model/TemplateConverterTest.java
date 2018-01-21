package org.opentosca.toscana.core.parse.model;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.node.DockerApplication;
import org.opentosca.toscana.model.requirement.DockerHostRequirement;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class TemplateConverterTest extends BaseUnitTest {

    @Test
    public void dockerConverter() {
        EffectiveModel model = new EffectiveModel(TestCsars.VALID_MINIMAL_DOCKER_TEMPLATE, log);
        assertNotNull(model);
        DockerApplication dockerApp = (DockerApplication) model.getNodeMap().get("simpleTaskApp");
        DockerHostRequirement host = dockerApp.getDockerHost();
        host.getFulfillers().stream().findFirst().get();
    }

    @Test
    public void lampNoInputConverter() {
        EffectiveModel model = new EffectiveModel(TestCsars.VALID_LAMP_NO_INPUT_TEMPLATE, log);
        assertNotNull(model);
    }

    @Test
    public void lampInputConverter() {
        EffectiveModel model = new EffectiveModel(TestCsars.VALID_LAMP_INPUT_TEMPLATE, log);
        assertNotNull(model);
    }
}
