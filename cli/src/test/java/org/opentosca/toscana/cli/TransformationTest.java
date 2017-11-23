package org.opentosca.toscana.cli;

import java.io.IOException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TransformationTest extends TestHelper {

    @Test
    public void TransformationDelete() throws IOException {
        apiDoubleInput(CSAR, PLATFORM, TRANSFORMATIONS_JSON, 200);
        assertEquals("", getApi().deleteTransformation(CSAR, PLATFORM));
    }

    @Test
    public void TransformationDeleteError() throws IOException {
        enqueError(400);
        assertEquals("", getApi().deleteTransformation(CSAR, PLATFORM));
    }

    @Test
    public void TransformationDownload() throws IOException {
        apiDoubleInput(CSAR, PLATFORM, TRANSFORMATION_JSON, 200);
        assertTrue(getApi().downloadTransformation(CSAR, PLATFORM).contains(CSAR));
    }

    @Test
    public void TransformationDownloadError() throws IOException {
        enqueError(400);
        getApi().downloadTransformation(CSAR, PLATFORM);
    }

    @Test
    public void TransformationInfo() throws IOException {
        apiDoubleInput(CSAR, PLATFORM, TRANSFORMATION_JSON, 200);
        assertTrue(getApi().infoTransformation(CSAR, PLATFORM).contains(PLATFORM));
    }

    @Test
    public void TransformationInfoError() throws IOException {
        enqueError(400);
        assertEquals("", getApi().infoTransformation(CSAR, PLATFORM));
    }

    @Test
    public void TransformationList() throws IOException {
        apiSingleInput(TRANSFORMATIONS_JSON, 200);
        assertEquals(PLATFORM, getApi().listTransformation(CSAR));
    }

    @Test
    public void TransformationListError() throws IOException {
        enqueError(400);
        assertEquals("", getApi().listTransformation(CSAR));
    }

    @Test
    public void TransformationLogs() throws IOException {
        apiDoubleInput(CSAR, PLATFORM, TRANSFORMATION_LOGS_JSON, 200);
        assertTrue(getApi().logsTransformation(CSAR, PLATFORM, 3).contains(LOGS_RESPONSE));
    }

    @Test
    public void TransformationLogsError() throws IOException {
        enqueError(400);
        assertEquals("", getApi().logsTransformation(CSAR, PLATFORM, 3));
    }

    @Test
    public void TransformationStart() throws IOException {
        apiDoubleInput(CSAR, PLATFORM, CSAR_JSON, 200);
        assertEquals("", getApi().startTransformation(CSAR, PLATFORM));
    }

    @Test
    public void TransformationStartError() throws IOException {
        enqueError(400);
        assertEquals("", getApi().startTransformation(CSAR, PLATFORM));
    }

    @Test
    public void TransformationStop() throws IOException {
        apiDoubleInput(CSAR, PLATFORM, TRANSFORMATIONS_JSON, 200);
        assertEquals("Aborting Transformation.", getApi().stopTransformation(CSAR, PLATFORM));
    }

    @Test
    public void TransformationStopError() throws IOException {
        enqueError(400);
        getApi().stopTransformation(CSAR, PLATFORM);
    }
}
