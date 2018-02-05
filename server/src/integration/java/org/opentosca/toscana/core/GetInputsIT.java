package org.opentosca.toscana.core;

import java.io.IOException;

import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.retrofit.BlockingToscanaApi;
import org.opentosca.toscana.retrofit.ToscanaApi;
import org.opentosca.toscana.retrofit.model.Transformation;
import org.opentosca.toscana.retrofit.util.TOSCAnaServerException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GetInputsIT extends BaseSpringIntegrationTest {

    /**
     Tests whether transformation inputs can be retrieved when the Transformation reached state DONE or ERROR.
     */
    @Test
    public void getInputsAfterTransformationTest() throws IOException, TOSCAnaServerException {
        ToscanaApi api = new BlockingToscanaApi(getHttpUrl());
        String csarName = "csarname";
        api.uploadCsar(csarName, TestCsars.VALID_EMPTY_TOPOLOGY);
        String platformId = "kubernetes";
        api.createTransformation(csarName, platformId);
        api.startTransformation(csarName, platformId);
        assertEquals(Transformation.TransformationState.DONE, api.getTransformation(csarName, platformId).getState());
        api.getInputs(csarName, platformId);
    }
}
