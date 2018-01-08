package org.opentosca.toscana.api.upload;

import org.opentosca.toscana.core.BaseSpringIntegrationTest;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.retrofit.TOSCAnaAPI;
import org.opentosca.toscana.retrofit.util.TOSCAnaServerException;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class CsarUploadIT extends BaseSpringIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(CsarUploadIT.class);

    private TOSCAnaAPI toscanaAPI;

    @Before
    public void setUp() throws Exception {
        toscanaAPI = new TOSCAnaAPI(getHttpUrl());
    }

    @Test(timeout = 30000)
    public void testFileUpload() throws Exception {
        System.err.println("Server started!");

        toscanaAPI.uploadCsar("test", TestCsars.VALID_EMPTY_TOPOLOGY);
    }

    @Test(timeout = 30000)
    public void testFileUploadFail() throws Exception {
        System.err.println("Server started");
        try {
            toscanaAPI.uploadCsar("test", TestCsars.INVALID_ENTRYPOINT_AMBIGUOUS);
        } catch (TOSCAnaServerException e) {
            e.getErrorResponse().getLogs().forEach(c -> logger.info(c.getMessage()));
            assertEquals(400, e.getStatusCode());
            return;
        }
        fail();
    }
}
