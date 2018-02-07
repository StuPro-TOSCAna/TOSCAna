package org.opentosca.toscana.core;

import java.io.IOException;

import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.retrofit.BlockingToscanaApi;
import org.opentosca.toscana.retrofit.ToscanaApi;
import org.opentosca.toscana.retrofit.model.TransformationOutputs;
import org.opentosca.toscana.retrofit.model.TransformationProperty;
import org.opentosca.toscana.retrofit.util.TOSCAnaServerException;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 Tests retrieving of tosca outputs via the rest api. This test case also verifies whether the outputs value
 was correctly set via value linking (get_property)
 */
public class OutputIT extends BaseSpringIntegrationTest {

    private ToscanaApi api;

    @Before
    public void setUp() {
        api = new BlockingToscanaApi(getHttpUrl());
    }

    @Test
    public void outputTest() throws IOException, TOSCAnaServerException {
        String csarName = "outputCsar";
        api.uploadCsar(csarName, TestCsars.VALID_OUTPUTS);
        String platformId = "kubernetes";
        api.createTransformation(csarName, platformId);
        api.startTransformation(csarName, platformId);
        TransformationOutputs outputs = api.getOutputs(csarName, platformId);
        TransformationProperty p = outputs.getProperties().iterator().next();
        assertEquals("8084", p.getValue());
    }
}
