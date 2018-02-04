package org.opentosca.toscana.core;

import java.io.IOException;

import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.retrofit.BlockingToscanaApi;
import org.opentosca.toscana.retrofit.ToscanaApi;
import org.opentosca.toscana.retrofit.model.Transformation;
import org.opentosca.toscana.retrofit.model.Transformation.TransformationState;
import org.opentosca.toscana.retrofit.util.TOSCAnaServerException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InputValidationIT extends BaseSpringIntegrationTest {

    /**
     Input properties marked as required but having a default value result in state READY
     */
    @Test
    public void noInputRequired() throws IOException, TOSCAnaServerException {
        ToscanaApi api = new BlockingToscanaApi(getHttpUrl());
        String csarName = "test_csar";
        api.uploadCsar(csarName, TestCsars.VALID_LAMP_NO_INPUT);
        String platformId = "kubernetes";
        api.createTransformation(csarName, platformId);
        Transformation t = api.getTransformation(csarName, platformId);
        assertEquals(TransformationState.READY, t.getState());
    }
}
