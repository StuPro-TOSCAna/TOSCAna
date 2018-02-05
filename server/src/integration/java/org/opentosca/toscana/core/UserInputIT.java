package org.opentosca.toscana.core;

import java.io.IOException;

import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryPlugin;
import org.opentosca.toscana.retrofit.BlockingToscanaApi;
import org.opentosca.toscana.retrofit.ToscanaApi;
import org.opentosca.toscana.retrofit.model.TransformationInputs;
import org.opentosca.toscana.retrofit.model.TransformationProperty;
import org.opentosca.toscana.retrofit.util.TOSCAnaServerException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.Assert.assertEquals;

/**
 Tests whether user inputs are correctly propagated into the EffectiveModel and provided for the transformation.
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserInputIT extends BaseSpringIntegrationTest {

    private ToscanaApi api;
    private String csarName = "lamp";
    private String platformId = new CloudFoundryPlugin().getPlatform().id;
    private TransformationInputs properties;

    @Before
    public void setUp() throws IOException, TOSCAnaServerException {
        this.api = new BlockingToscanaApi(getHttpUrl());
        api.uploadCsar(csarName, TestCsars.VALID_LAMP_INPUT);
        api.createTransformation(csarName, platformId);
        properties = api.getInputs(csarName, platformId);
    }

    @Test
    public void transformationWithModelInputs() throws IOException, TOSCAnaServerException {
        for (TransformationProperty property : properties.getInputs()) {
            property.setValue("1");
        }
        api.updateProperties(csarName, platformId, properties);
        // if call does not fail, inputs have been correctly set and propagated
        api.startTransformation(csarName, platformId);
    }

    @Test
    public void setInputsToNullTest() throws IOException, TOSCAnaServerException {
        this.properties.getInputs().stream().forEach(p -> p.setValue(null));
        api.updateProperties(csarName, platformId, properties);
        TransformationInputs newProperties = api.getInputs(csarName, platformId);
        for (int i = 0; i < this.properties.getInputs().size(); i++) {
            assertEquals(this.properties.getInputs().get(i).getValue(),
                newProperties.getInputs().get(i).getValue());
        }
    }
}
