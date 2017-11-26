package org.opentosca.toscana.cli;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CsarTest extends TestHelper {

    @Test
    public void CsarDelete() throws IOException {
        apiSingleInput(CSAR_JSON, 200);
        assertEquals("", api.deleteCsar(CSAR));
    }

    @Test
    public void CsarDeleteError() throws IOException {
        enqueError(404);
        api.deleteCsar(CSAR);
    }

    @Test
    public void CsarInfo() throws IOException {
        apiSingleInput(CSAR_JSON, 200);
        assertEquals(CSAR, api.infoCsar(CSAR));
    }

    @Test
    public void CsarInfoError() throws IOException {
        enqueError(404);
        api.infoCsar(CSAR);
    }

    @Test
    public void CsarList() throws IOException {
        apiSingleInput(CSARS_JSON, 200);
        assertTrue(api.listCsar().contains(CSAR));
    }

    @Test
    public void CsarListError() throws IOException {
        enqueError(404);
        assertEquals("", api.listCsar());
    }

    @Test
    public void CsarUpload() throws IOException {
        apiSingleInput(CSAR_JSON, 200);
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("csar/simple-task.csar").getFile());
        assertEquals("", api.uploadCsar(file));
    }

    @Test
    public void CsarUploadError() throws IOException {
        enqueError(404);
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("csar/simple-task.csar").getFile());
        assertEquals("", api.uploadCsar(file));
    }
}
