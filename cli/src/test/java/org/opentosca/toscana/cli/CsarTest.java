package org.opentosca.toscana.cli;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CsarTest extends TestHelper {

    @Test
    public void csarDelete() throws IOException {
        apiSingleInput(CSAR_JSON, 200);
        assertEquals("", api.deleteCsar(CSAR));
    }

    @Test
    public void csarDeleteError() throws IOException {
        enqueError(404);
        assertEquals("", api.deleteCsar(CSAR));
    }

    @Test
    public void csarInfo() throws IOException {
        apiSingleInput(CSAR_JSON, 200);
        assertEquals(CSAR, api.infoCsar(CSAR));
    }

    @Test
    public void csarInfoError() throws IOException {
        enqueError(404);
        assertEquals("", api.infoCsar(CSAR));
    }

    @Test
    public void csarList() throws IOException {
        apiSingleInput(CSARS_JSON, 200);
        assertTrue(api.listCsar().contains(CSAR));
    }

    @Test
    public void csarListError() throws IOException {
        enqueError(404);
        assertEquals("", api.listCsar());
    }

    @Test
    public void csarUpload() throws IOException {
        apiSingleInput(CSAR_JSON, 200);
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("csar/simple-task.csar").getFile());
        assertEquals("", api.uploadCsar(file));
    }

    @Test
    public void csarUploadError() throws IOException {
        enqueError(404);
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("csar/simple-task.csar").getFile());
        assertEquals("", api.uploadCsar(file));
    }
}
