package org.opentosca.toscana;

import java.io.IOException;
import java.util.Map;

import org.opentosca.toscana.core.BaseSpringIntegrationTest;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryPlugin;
import org.opentosca.toscana.retrofit.TOSCAnaAPI;
import org.opentosca.toscana.retrofit.model.Csar;
import org.opentosca.toscana.retrofit.model.Transformation;
import org.opentosca.toscana.retrofit.model.TransformationProperties;
import org.opentosca.toscana.retrofit.model.TransformationProperty;
import org.opentosca.toscana.retrofit.util.TOSCAnaServerException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 Tests whether user inputs are correctly propagated into the EffectiveModel and provided for the transformation.
 */
public class UserInputIT extends BaseSpringIntegrationTest {

    @Test
    public void transformationWithModelInputs() throws IOException, TOSCAnaServerException {
        TOSCAnaAPI api = new TOSCAnaAPI(getHttpUrl());
        String csarName = "lamp";
        api.uploadCsar(csarName, TestCsars.VALID_LAMP_INPUT);
        String platformId = new CloudFoundryPlugin().getPlatform().id;
        api.createTransformation(csarName, platformId);
        TransformationProperties properties = api.getProperties(csarName, platformId);
        for (TransformationProperty property : properties.getProperties()){
            property.setValue("1");
        }
        api.startTransformation(csarName, platformId);
    }
}
