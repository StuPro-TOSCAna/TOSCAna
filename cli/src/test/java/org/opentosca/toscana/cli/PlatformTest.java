package org.opentosca.toscana.cli;

import java.io.IOException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PlatformTest extends TestHelper {

    @Test
    public void PlatformInfo() throws IOException {
        apiSingleInput(PLATFORM_JSON, 200);
        assertTrue(api.infoPlatform(PLATFORM).contains(PLATFORM));
    }

    @Test
    public void PlatformInfoError() throws IOException {
        enqueError(400);
        assertEquals("", api.infoPlatform(PLATFORM));
    }

    @Test
    public void PlatformList() throws IOException {
        apiSingleInput(PLATFORMS_JSON, 200);
        assertTrue(api.listPlatform().contains(PLATFORM));
    }

    @Test
    public void PlatformListError() throws IOException {
        enqueError(400);
        assertEquals("", api.listPlatform());
    }
}
