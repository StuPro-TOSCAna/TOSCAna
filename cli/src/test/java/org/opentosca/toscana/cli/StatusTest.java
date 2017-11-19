package org.opentosca.toscana.cli;

import java.io.IOException;

import org.opentosca.toscana.cli.commands.Constants;

import org.junit.After;
import org.junit.Before;
import picocli.CommandLine;

public class StatusTest {

    private ApiController api = null;
    private CommandLine cmd = null;
    private TestHelper helper = null;
    private Constants con = null;

    @Before
    public void setUp() throws IOException {
        api = new ApiController(ApiController.Mode.NONE);
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
/*
    @Test
    public void testCliStatus() throws IOException {
        helper.setServerBody("systemstatus");
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.STATUS);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testSystemStatus() throws IOException {
        helper.setServerBody("systemstatus");
        assertTrue(api.showStatus().contains(con.STATUS_SUCCESS));
    }
    */
}
