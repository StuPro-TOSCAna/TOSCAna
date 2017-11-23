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

public class PlatformTest {

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
    public void testCliPlatform() {
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.PLATFORM_AR);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testPlatformList() throws IOException {
        helper.setServerBody("platformlist");
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.PLATFORM_LIST);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testPlatformInfo() throws IOException {
        helper.setServerBody("platforminfo");
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.PLATFORM_INFO);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testPlatformsList() throws IOException {
        helper.setServerBody("platformlist");
        assertTrue(api.listPlatform().contains(con.PLATFORM_LIST_SUCCESS));
    }

    @Test
    public void testInfoPlatform() throws IOException {
        helper.setServerBody("platforminfo");
        assertTrue(api.infoPlatform(helper.PLATFORM).contains(con.PLATFORM_INFO_SUCCESS));
    }

    @Test
    public void testFail404InfoPlatform() throws IOException {
        helper.server404Response();
        assertEquals(con.PLATFORM_INFO_ERROR404 + "\n", api.infoPlatform(helper.PLATFORM));
    }
}
