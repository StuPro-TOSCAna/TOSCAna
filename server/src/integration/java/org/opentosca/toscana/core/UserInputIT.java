package org.opentosca.toscana.core;

import java.io.IOException;

import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryPlugin;
import org.opentosca.toscana.retrofit.BlockingToscanaApi;
import org.opentosca.toscana.retrofit.ToscanaApi;
import org.opentosca.toscana.retrofit.model.TransformationProperties;
import org.opentosca.toscana.retrofit.model.TransformationProperty;
import org.opentosca.toscana.retrofit.util.TOSCAnaServerException;

import org.junit.Test;

/**
 Tests whether user inputs are correctly propagated into the EffectiveModel and provided for the transformation.
 */
public class UserInputIT extends BaseSpringIntegrationTest {

    @Test
    public void transformationWithModelInputs() throws IOException, TOSCAnaServerException {
        ToscanaApi api = new BlockingToscanaApi(getHttpUrl());
        String csarName = "lamp";
        api.uploadCsar(csarName, TestCsars.VALID_LAMP_INPUT);
        String platformId = new CloudFoundryPlugin().getPlatform().id;
        api.createTransformation(csarName, platformId);
        TransformationProperties properties = api.getProperties(csarName, platformId);
        for (TransformationProperty property : properties.getProperties()) {
            property.setValue("1");
        }
        api.updateProperties(csarName, platformId, properties);
        // if call does not fail, inputs have been correctly set and propagated
        api.startTransformation(csarName, platformId);
    }
}
