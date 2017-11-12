package org.opentosca.toscana.cli;

import java.io.IOException;
import java.util.List;

import org.opentosca.toscana.cli.commands.Constants;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class TransformationTest {

    private ApiController api = null;
    private CommandLine cmd = null;
    private TestHelper helper = null;
    private Constants con = null;

    @Before
    public void setUp() throws IOException {
        api = new ApiController(false, false);
        CliMain cli = new CliMain();
        cmd = new CommandLine(cli);
        helper = new TestHelper();
        helper.setUp();
        con = new Constants();
    }

    @After
    public void tearDown() throws IOException {
        helper.tearDown();
    }

    @Test
    public void testCliTransformation() {
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.TRANSFORMATION_AR);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testTransformationDelete2() throws IOException {
        helper.setServerBody("transformationlist");
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.TRANSFORMATION_DELETE);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testTransformationDeleteTransInput() throws IOException {
        helper.setServerBody("transformationlist");
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.TRANSFORMATION_DELETE_T_VALID);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testTransformationDeleteNoInput() throws IOException {
        helper.setServerBody("transformationlist");
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.TRANSFORMATION_DELETE_NOINPUT);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testTransformationDeleteError() throws IOException {
        helper.setServerBody("transformationlist");
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.TRANSFORMATION_DELETE_T_NOTVALID);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testTransformationDownload() throws IOException {
        helper.setServerBody("transformationartifact");
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.TRANSFORMATION_DOWNLOAD);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testTransformationDownloadTransInput() throws IOException {
        helper.setServerBody("transformationartifact");
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.TRANSFORMATION_DOWNLOAD_T_VALID);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testTransformationDownloadNoInput() throws IOException {
        helper.setServerBody("transformationartifact");
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.TRANSFORMATION_DOWNLOAD_NOINPUT);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testTransformationDownloadError() throws IOException {
        helper.setServerBody("transformationartifact");
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.TRANSFORMATION_DOWNLOAD_T_NOTVALID);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testTransformationInfo2() throws IOException {
        helper.setServerBody("transformationinfo");
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.TRANSFORMATION_INFO);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testTransformationInfoTransInput() throws IOException {
        helper.setServerBody("transformationinfo");
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.TRANSFORMATION_INFO_T_VALID);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testTransformationInfoNoInput() throws IOException {
        helper.setServerBody("transformationinfo");
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.TRANSFORMATION_INFO_NOINPUT);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testTransformationInfoError() throws IOException {
        helper.setServerBody("transformationinfo");
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.TRANSFORMATION_INFO_T_NOTVALID);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testTransformationList2() throws IOException {
        helper.setServerBody("transformationlist");
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.TRANSFORMATION_LIST);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testTransformationLogs2() throws IOException {
        helper.setServerBody("transformationlogs");
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.TRANSFORMATION_LOGS);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testTransformationLogsTransInput() throws IOException {
        helper.setServerBody("transformationlogs");
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.TRANSFORMATION_LOGS_T_VALID);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testTransformationLogsNotProvided() throws IOException {
        helper.setServerBody("transformationlogs");
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.TRANSFORMATION_LOGS_NOINPUT);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testTransformationLogsError() throws IOException {
        helper.setServerBody("transformationlogs");
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.TRANSFORMATION_LOGS_T_NOTVALID);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testTransformationStart() throws IOException {
        helper.setServerBody("transformationlist");
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.TRANSFORMATION_START);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testTransformationStartTransInput() throws IOException {
        helper.setServerBody("transformationlist");
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.TRANSFORMATION_START_T_VALID);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testTransformationStartNoInput() throws IOException {
        helper.setServerBody("transformationlist");
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.TRANSFORMATION_START_NOINPUT);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testTransformationStartError() throws IOException {
        helper.setServerBody("transformationlist");
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.TRANSFORMATION_START_T_NOTVALID);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testTransformationStop() {
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.TRANSFORMATION_STOP);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testTransformationStopTransInput() {
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.TRANSFORMATION_STOP_T_VALID);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testTransformationStopNoInput() {
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.TRANSFORMATION_STOP_NOINPUT);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testTransformationStopError() {
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.TRANSFORMATION_STOP_T_NOTVALID);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testStopTransformation() throws IOException {
        assertEquals(con.TRANSFORMATION_STOP, api.stopTransformation(helper.CSAR, helper.PLATFORM));
    }

    @Test
    public void testTransformationList() throws IOException {
        helper.setServerBody("transformationlist");
        assertTrue(api.listTransformation(helper.CSAR).contains(con.TRANSFORMATION_LIST_SUCCESS));
    }

    @Test
    public void testFail404TransformationList() throws IOException {
        helper.server404Response();
        assertEquals(con.TRANSFORMATION_LIST_ERROR404M, api.listTransformation(helper.CSAR));
    }

    @Test
    public void testTransformationInfo() throws IOException {
        helper.setServerBody("transformationinfo");
        assertTrue(api.infoTransformation(helper.CSAR, helper.PLATFORM).contains(con.TRANSFORMATION_INFO_SUCCESS));
    }

    @Test
    public void testFail404TransformationInfo() throws IOException {
        helper.server404Response();
        assertEquals(con.TRANSFORMATION_INFO_ERROR404M, api.infoTransformation(helper.CSAR, helper.PLATFORM));
    }

    @Test
    public void testTransformationLogs() throws IOException {
        helper.setServerBody("transformationlogs");
        assertTrue(api.logsTransformation(helper.CSAR, helper.PLATFORM, 8).contains(con.TRANSFORMATION_LOGS_SUCCESS));
    }

    @Test
    public void testFail404TransformationLogs() throws IOException {
        helper.server404Response();
        assertTrue(api.logsTransformation(helper.CSAR, helper.PLATFORM, 8).contains(con.TRANSFORMATION_LOGS_ERROR404));
    }

    @Test
    public void testTransformationArtifacts() throws IOException {
        helper.setServerBody("transformationartifact");
        assertTrue(api.downloadTransformation(helper.CSAR, helper.PLATFORM).contains(con.TRANSFORMATION_DOWNLOAD_SUCCESS));
    }

    @Test
    public void testFail400TransformationArtifacts() throws IOException {
        helper.server400Response();
        assertTrue(api.downloadTransformation(helper.CSAR, helper.PLATFORM).contains(con.TRANSFORMATION_DOWNLOAD_ERROR400));
    }

    @Test
    public void testFail404TransformationArtifacts() throws IOException {
        helper.server404Response();
        assertTrue(api.downloadTransformation(helper.CSAR, helper.PLATFORM).contains(con.TRANSFORMATION_DOWNLOAD_ERROR404));
    }

    /**
     * @Test public void testStartTransformation() throws IOException {
     * helper.setServerBody("csarlist");
     * assertEquals(con.TRANSFORMATION_START_SUCCESS, api.startTransformation(helper.CSAR, helper.PLATFORM));
     * }
     **/

    @Test
    public void testFail400StartTransformation() throws IOException {
        helper.server400Response();
        assertTrue(api.startTransformation(helper.CSAR, helper.PLATFORM).contains(con.TRANSFORMATION_START_ERROR400));
    }

    @Test
    public void testFail404StartTransformation() throws IOException {
        helper.server404Response();
        assertTrue(api.startTransformation(helper.CSAR, helper.PLATFORM).contains(con.TRANSFORMATION_START_ERROR404));
    }

    @Test
    public void testTransformationDelete() throws IOException {
        helper.setServerBody("transformationlist");
        assertEquals(con.TRANSFORMATION_DELETE_SUCCESS, api.deleteTransformation(helper.CSAR, helper.PLATFORM));
    }

    @Test
    public void testFail404TransformationDelete() throws IOException {
        helper.server404Response();
        assertEquals(con.TRANSFORMATION_DELETE_ERROR404M, api.deleteTransformation(helper.CSAR, helper.PLATFORM));
    }

    @Test
    public void testFail500TransformationDelete() throws IOException {
        helper.server500Response();
        assertEquals(con.TRANSFORMATION_DELETE_ERROR500M, api.deleteTransformation(helper.CSAR, helper.PLATFORM));
    }
}
