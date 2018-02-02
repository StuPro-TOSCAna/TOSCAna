package org.opentosca.toscana.core;

import java.io.IOException;

import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryPlugin;
import org.opentosca.toscana.retrofit.BlockingToscanaApi;
import org.opentosca.toscana.retrofit.TOSCAnaAPI;
import org.opentosca.toscana.retrofit.model.TransformationProperties;
import org.opentosca.toscana.retrofit.model.TransformationProperty;
import org.opentosca.toscana.retrofit.util.TOSCAnaServerException;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 Tests whether user inputs are correctly propagated into the EffectiveModel and provided for the transformation.
 */
public class UserInputIT extends BaseSpringIntegrationTest {

    private TOSCAnaAPI api;
    String csarName = "lamp";
    String platformId = new CloudFoundryPlugin().getPlatform().id;
    TransformationProperties properties;

    @Before
    public void setUp() throws IOException, TOSCAnaServerException {
        this.api = new BlockingToscanaApi(getHttpUrl());
        api.uploadCsar(csarName, TestCsars.VALID_LAMP_INPUT);
        api.createTransformation(csarName, platformId);
        properties = api.getProperties(csarName, platformId);
    }

    @Test
    public void transformationWithModelInputs() throws IOException, TOSCAnaServerException {
        for (TransformationProperty property : properties.getProperties()) {
            property.setValue("1");
        }
        api.updateProperties(csarName, platformId, properties);
        // if call does not fail, inputs have been correctly set and propagated
        api.startTransformation(csarName, platformId);
    }

    @Test
    public void setInputsToNullTest() throws IOException, TOSCAnaServerException {
        this.properties.getProperties().stream().forEach(p -> p.setValue(null));
        api.updateProperties(csarName, platformId, properties);
        TransformationProperties newProperties = api.getProperties(csarName, platformId);
        for (int i = 0; i < this.properties.getProperties().size(); i++) {
            assertEquals(this.properties.getProperties().get(i).getValue(),
                newProperties.getProperties().get(i).getValue());
        }
    }
}
