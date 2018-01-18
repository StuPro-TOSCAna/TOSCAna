package org.opentosca.toscana.core.parse.converter;

import org.opentosca.toscana.core.BaseIntegrationTest;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.node.DockerApplication;
import org.opentosca.toscana.model.requirement.DockerHostRequirement;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 Tests the conversion of the minimal-docker csar to an effective model
 */
public class ModelInstanceConverterIT extends BaseIntegrationTest {

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

    // TODO uncomment this after implementing fake input setup for unit testing
//    @Test
//    public void lampInputConverter() throws Exception {
//        EffectiveModel model = new EffectiveModel(TestCsars.VALID_LAMP_INPUT_TEMPLATE);
//        assertNotNull(model);
//    }
}
