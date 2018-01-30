package org.opentosca.toscana.core;

import java.io.IOException;

import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.retrofit.BlockingToscanaApi;
import org.opentosca.toscana.retrofit.TOSCAnaAPI;
import org.opentosca.toscana.retrofit.model.Transformation;
import org.opentosca.toscana.retrofit.model.Transformation.TransformationState;
import org.opentosca.toscana.retrofit.util.TOSCAnaServerException;

import okhttp3.ResponseBody;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PropertyValidationIT extends BaseSpringIntegrationTest {

    @Test
    public void lampNoinputPropertyValidationTest() throws IOException, TOSCAnaServerException {
        TOSCAnaAPI api = new BlockingToscanaApi(getHttpUrl());
        String csarName = "test_csar";
        api.uploadCsar(csarName, TestCsars.VALID_LAMP_NO_INPUT);
        String platformId = "kubernetes";
        api.createTransformation(csarName, platformId);
        Transformation t = api.getTransformation(csarName, platformId);
        assertEquals(TransformationState.INPUT_REQUIRED, t.getStatus());
        ResponseBody response = api.startTransformation(csarName, platformId);
        return;
    }
}
