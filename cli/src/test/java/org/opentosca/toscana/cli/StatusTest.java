package org.opentosca.toscana.cli;

import java.io.IOException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StatusTest extends TestHelper {

    @Test
    public void CliStatus() throws IOException {
        apiSingleInput(STATUS_HEALTH_JSON, 200);
        assertTrue(api.showStatus().contains("UP"));
    }

    @Test
    public void CliStatusError() throws IOException {
        enqueError(400);
        assertEquals("", api.showStatus());
    }
}
