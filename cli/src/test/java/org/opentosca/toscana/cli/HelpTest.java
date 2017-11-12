package org.opentosca.toscana.cli;

import java.util.ArrayList;
import java.util.List;

import org.opentosca.toscana.cli.commands.ToscanaHelp;
import org.opentosca.toscana.cli.restclient.model.Csar;
import org.opentosca.toscana.cli.restclient.model.Csars;
import org.opentosca.toscana.cli.restclient.model.CsarsResponse;
import org.opentosca.toscana.cli.restclient.model.Platform;
import org.opentosca.toscana.cli.restclient.model.Platforms;
import org.opentosca.toscana.cli.restclient.model.PlatformsResponse;
import org.opentosca.toscana.cli.restclient.model.Status;
import org.opentosca.toscana.cli.restclient.model.Transformation;
import org.opentosca.toscana.cli.restclient.model.TransformationArtifact;
import org.opentosca.toscana.cli.restclient.model.TransformationInput;
import org.opentosca.toscana.cli.restclient.model.TransformationInputs;
import org.opentosca.toscana.cli.restclient.model.TransformationLog;
import org.opentosca.toscana.cli.restclient.model.TransformationLogs;
import org.opentosca.toscana.cli.restclient.model.Transformations;
import org.opentosca.toscana.cli.restclient.model.TransformationsResponse;

import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;

import static org.junit.Assert.assertEquals;

public class HelpTest {

    private final List<Csar> cList = new ArrayList<>();
    private final List<Platform> pList = new ArrayList<>();
    private final List<Transformation> tList = new ArrayList<>();
    private final List<TransformationLog> tLogss = new ArrayList<>();
    private final List<TransformationInput> tProperties = new ArrayList<>();
    private final Csar csar = new Csar("csar");
    private final Csars csars = new Csars(cList);
    private final CsarsResponse cResponse = new CsarsResponse(csars);
    private final Platform plat = new Platform("id", "plat");
    private final Platforms plats = new Platforms(pList);
    private final PlatformsResponse pResponse = new PlatformsResponse(plats);
    private final Status status = new Status("idle", 1000L, 1000L);
    private final Transformation tran = new Transformation("working", "aws", "idle");
    private final Transformations trans = new Transformations(tList);
    private final TransformationArtifact tArt = new TransformationArtifact("test.de");
    private final TransformationLog tLog = new TransformationLog(100L, "message", "3");
    private final TransformationLogs tLogs = new TransformationLogs(0, 0, tLogss);
    private final TransformationInput tProp = new TransformationInput("key", "type");
    private final TransformationInput tProp2 = new TransformationInput("key", true);
    private final TransformationInputs tProps = new TransformationInputs(tProperties);
    private final TransformationsResponse tResponse = new TransformationsResponse(trans);
    private CommandLine cmd = null;

    @Before
    public void setUp() {
        CliMain cli = new CliMain();
        cmd = new CommandLine(cli);
    }

    @Test
    public void testCliStart() {
        CliMain.main(new String[] {""});
    }

    @Test
    public void testToscanaHelp() {
        final List<CommandLine> parsed = cmd.parse("help");
        assertEquals("help", parsed.get(1).getCommandName());
    }

    @Test
    public void testHelpPlatform() {
        final String[] content = {"help", "platform"};
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, content);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testHelpTransformationDownload() {
        final String[] content = {"help", "transformation", "download"};
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, content);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testFailHelpShort() {
        final String[] content = {};
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, content);
        new ToscanaHelp().run();
        assertEquals(1, parsed.size());
    }
}
