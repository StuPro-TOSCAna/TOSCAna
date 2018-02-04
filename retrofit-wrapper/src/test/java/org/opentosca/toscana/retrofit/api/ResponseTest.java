package org.opentosca.toscana.retrofit.api;

import java.util.Arrays;
import java.util.Map;

import org.opentosca.toscana.retrofit.model.ServerError;
import org.opentosca.toscana.retrofit.model.TransformationInputs;
import org.opentosca.toscana.retrofit.model.TransformationProperty;
import org.opentosca.toscana.retrofit.model.TransformationProperty.PropertyType;
import org.opentosca.toscana.retrofit.model.TransformerStatus;
import org.opentosca.toscana.retrofit.model.embedded.CsarResources;
import org.opentosca.toscana.retrofit.util.TOSCAnaServerException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(JUnit4.class)
public class ResponseTest extends BaseToscanaApiTest {
    @Test
    public void getStatus() throws Exception {
        enqueResponse("json/health.json", 200, "application/json");
        TransformerStatus status = api.getServerStatus();
        assertEquals("UP", status.getStatus());
    }

    //TODO Consider fixing this test after the property update
    @Test
    public void setInvalidPropertiesTest() throws Exception {
        enqueResponse("json/set_invalid_inputs.json", 406, "application/json");
        TransformationProperty property = new TransformationProperty();
        property.setType(PropertyType.UNSIGNED_INTEGER);
        property.setKey("unsigned_integer");
        property.setValue("-11");
        TransformationInputs properties = new TransformationInputs(Arrays.asList(property));
        Map<String, Boolean> res = api.updateProperties("test", "test", properties);
        assertEquals(false, res.get("unsigned_integer"));
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
