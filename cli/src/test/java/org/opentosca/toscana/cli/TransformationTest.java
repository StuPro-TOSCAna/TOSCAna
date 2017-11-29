package org.opentosca.toscana.cli;

import java.io.IOException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TransformationTest extends TestHelper {

    @Test
    public void transformationDelete() throws IOException {
        apiDoubleInput(CSAR, PLATFORM, TRANSFORMATIONS_JSON, 200);
        assertEquals("", api.deleteTransformation(CSAR, PLATFORM));
    }

    @Test
    public void transformationDeleteError() throws IOException {
        enqueError(400);
        assertEquals("", api.deleteTransformation(CSAR, PLATFORM));
    }

    @Test
    public void transformationDownload() throws IOException {
        apiDoubleInput(CSAR, PLATFORM, TRANSFORMATION_JSON, 200);
        assertTrue(api.downloadTransformationUrl(CSAR, PLATFORM).contains(CSAR));
    }

    @Test
    public void transformationDownloadError() throws IOException {
        enqueError(400);
        assertTrue(api.downloadTransformationUrl(CSAR, PLATFORM).contains(CSAR));
    }

    @Test
    public void transformationInfo() throws IOException {
        apiDoubleInput(CSAR, PLATFORM, TRANSFORMATION_JSON, 200);
        assertTrue(api.infoTransformation(CSAR, PLATFORM).contains(PLATFORM));
    }

    @Test
    public void transformationInfoError() throws IOException {
        enqueError(400);
        assertEquals("", api.infoTransformation(CSAR, PLATFORM));
    }

    @Test
    public void transformationList() throws IOException {
        apiSingleInput(TRANSFORMATIONS_JSON, 200);
        assertEquals(PLATFORM, api.listTransformation(CSAR));
    }

    @Test
    public void transformationListError() throws IOException {
        enqueError(400);
        assertEquals("", api.listTransformation(CSAR));
    }

    @Test
    public void transformationLogs() throws IOException {
        apiDoubleInput(CSAR, PLATFORM, TRANSFORMATION_LOGS_JSON, 200);
        assertTrue(api.logsTransformation(CSAR, PLATFORM, 3).contains(LOGS_RESPONSE));
    }

    @Test
    public void transformationLogsError() throws IOException {
        enqueError(400);
        assertEquals("", api.logsTransformation(CSAR, PLATFORM, 3));
    }

    @Test
    public void transformationStart() throws IOException {
        apiDoubleInput(CSAR, PLATFORM, CSAR_JSON, 200);
        assertEquals("", api.startTransformation(CSAR, PLATFORM));
    }

    @Test
    public void transformationStartError() throws IOException {
        enqueError(400);
        assertEquals("", api.startTransformation(CSAR, PLATFORM));
    }

    @Test
    public void transformationStop() throws IOException {
        apiDoubleInput(CSAR, PLATFORM, TRANSFORMATIONS_JSON, 200);
        assertEquals("Aborting Transformation.", api.stopTransformation(CSAR, PLATFORM));
    }

    @Test
    public void transformationStopError() throws IOException {
        enqueError(400);
        assertEquals("Aborting Transformation.", api.stopTransformation(CSAR, PLATFORM));
    }
}
