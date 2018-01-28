package org.opentosca.toscana.core;

import java.io.IOException;

import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.retrofit.ToscanaApi;
import org.opentosca.toscana.retrofit.model.TransformationOutputs;
import org.opentosca.toscana.retrofit.model.TransformationProperties;
import org.opentosca.toscana.retrofit.util.TOSCAnaServerException;

import org.junit.Before;
import org.junit.Test;

/**
 Tests retrieving of tosca outputs via the rest api. This test case also verifies whether the outputs value
 was correctly set via value linking (get_property)
 */
public class OutputPropertyIT extends BaseSpringIntegrationTest {

    private ToscanaApi api;

    @Before
    public void setUp() {
        api = new ToscanaApi(getHttpUrl());
    }

    @Test
    public void outputTest() throws IOException, TOSCAnaServerException, InterruptedException {
        String csarName = "outputCsar";
        api.uploadCsar(csarName, TestCsars.VALID_OUTPUTS);
        String platformId = "kubernetes";
        api.createTransformation(csarName, platformId);
        api.startTransformation(csarName, platformId);
        while (api.getTransformation(csarName, platformId).getStatus().equals("TRANSFORMING")) {
            Thread.sleep(5);
        }
        TransformationOutputs outputs = api.getOutputs(csarName, platformId);
    }
}
