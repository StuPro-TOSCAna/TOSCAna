package org.opentosca.toscana.retrofit.api;

import java.util.Map;

import org.opentosca.toscana.retrofit.model.ServerError;
import org.opentosca.toscana.retrofit.model.TransformerStatus;
import org.opentosca.toscana.retrofit.model.embedded.CsarResources;
import org.opentosca.toscana.retrofit.util.TOSCAnaServerException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(JUnit4.class)
public class ResponseTest extends BaseTOSCAnaAPITest {
    @Test
    public void getStatus() throws Exception {
        enqueResponse("json/health.json", 200, "application/json");
        TransformerStatus status = api.getServerStatus();
        assertEquals("UP", status.getStatus());
    }

    @Test
    public void getMetrics() throws Exception {
        enqueResponse("json/metrics.json", 200, "application/json");
        Map<String, Object> values = api.getTransformerMetrics();
        assertTrue(values.size() > 5);
    }

    @Test
    public void getCsars() throws Exception {
        enqueResponse("json/csars.json", 200, "application/json");
        CsarResources res = api.getCsars();
        assertTrue(res.getContent().size() > 1);
    }

    @Test
    public void csarUploadError() throws Exception {
        enqueResponse("json/parse_error.json", 400, "application/json");
        try {
            api.uploadCsar("test", new byte[100]);
        } catch (TOSCAnaServerException e) {
            assertEquals(400, ((TOSCAnaServerException) e).getStatusCode());
            ServerError error = ((TOSCAnaServerException) e).getErrorResponse();
            assertSame("", error.getMessage());
            return;
        }
        fail();
    }
}
