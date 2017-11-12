package org.opentosca.toscana.cli;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.opentosca.toscana.cli.commands.Constants;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class CsarTest {

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
    public void testCsar() {
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.CSAR_AR);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testCsarDelete() throws IOException {
        helper.setServerBody("csarlist");
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.CSAR_DELETE);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testDeleteCsar() throws IOException {
        helper.server200Response();
        assertEquals(con.CSAR_DELETE_SUCCESS, api.deleteCsar(helper.CSAR));
    }

    @Test
    public void testFail404DeleteCsar() throws IOException {
        helper.server404Response();
        assertEquals(con.CSAR_DELETE_ERROR404M, api.deleteCsar(helper.CSAR));
    }

    @Test
    public void testFail500DeleteCsar() throws IOException {
        helper.server500Response();
        assertEquals(con.CSAR_DELETE_ERROR500M, api.deleteCsar(helper.CSAR));
    }

    @Test
    public void testCsarInfo() throws IOException {
        helper.setServerBody("csarinfo");
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.CSAR_INFO);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testCsarList() throws IOException {
        helper.setServerBody("csarlist");
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.CSAR_LIST);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testCsarsList() throws IOException {
        helper.setServerBody("csarlist");
        assertTrue(api.listCsar().contains(con.CSAR_LIST_SUCCESS));
    }

    @Test
    public void testInfoCsar() throws IOException {
        helper.setServerBody("csarinfo");
        assertTrue(api.infoCsar(helper.CSAR).contains(con.CSAR_INFO_SUCCESS));
    }

    @Test
    public void testFail404InfoCsar() throws IOException {
        helper.server404Response();
        assertEquals(con.CSAR_INFO_ERROR404M, api.infoCsar(helper.CSAR));
    }

    @Test
    public void testCsarUpload() throws IOException {
        helper.setServerBody("csarlist");
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.CSAR_UPLOAD);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testUploadCsar() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("csar/simple-task.csar").getFile());
        helper.server201Response();
        assertEquals(con.CSAR_UPLOAD_SUCCESS, api.uploadCsar(file));
    }

    @Test
    public void testFail400UploadCsar() throws IOException {
        helper.server400Response();
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("csar/simple-task.csar").getFile());
        assertEquals(con.CSAR_UPLOAD_ERROR400M, api.uploadCsar(file));
    }

    @Test
    public void testFail500UploadCsar() throws IOException {
        helper.server500Response();
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("csar/simple-task.csar").getFile());
        assertEquals(con.CSAR_UPLOAD_ERROR500, api.uploadCsar(file));
    }
}
