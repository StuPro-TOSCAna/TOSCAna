package org.opentosca.toscana.api.upload;

import org.opentosca.toscana.core.BaseSpringIntegrationTest;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.retrofit.TOSCAnaAPI;

import org.junit.Before;
import org.junit.Test;

public class CsarUploadIT extends BaseSpringIntegrationTest {

    private TOSCAnaAPI toscanaAPI;

    @Before
    public void setUp() {
        toscanaAPI = new TOSCAnaAPI(getHttpUrl());
    }

    @Test(timeout = 30000)
    public void testFileUpload() throws Exception {
        System.err.println("Server started!");

        toscanaAPI.uploadCsar("test", TestCsars.VALID_EMPTY_TOPOLOGY);
    }
}
