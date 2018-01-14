package org.opentosca.toscana.core.parse.converter;

import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.node.DockerApplication;
import org.opentosca.toscana.model.requirement.DockerHostRequirement;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 Tests the conversion of the minimal-docker csar to an effective model
 */
public class ModelInstanceConverterIT {

    @Test
    public void dockerConverter() throws Exception {
        EffectiveModel model = new EffectiveModel(TestCsars.VALID_MINIMAL_DOCKER_TEMPLATE);
        assertNotNull(model);
        DockerApplication dockerApp = (DockerApplication) model.getNodeMap().get("simpleTaskApp");
        DockerHostRequirement host = dockerApp.getDockerHost();
        host.getFulfillers().stream().findFirst().get();
    }

    @Test
    public void lampNoInputConverter() throws Exception {
        EffectiveModel model = new EffectiveModel(TestCsars.VALID_LAMP_NO_INPUT_TEMPLATE);
        assertNotNull(model);
    }

    @Test
    public void lampInputConverter() throws Exception {
        EffectiveModel model = new EffectiveModel(TestCsars.VALID_LAMP_INPUT_TEMPLATE);
        assertNotNull(model);
    }
}
