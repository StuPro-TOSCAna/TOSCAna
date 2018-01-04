package org.opentosca.toscana.cli.commands;

import java.util.HashMap;
import java.util.Map;

import org.opentosca.toscana.cli.ApiController;
import org.opentosca.toscana.cli.commands.csar.CsarDelete;
import org.opentosca.toscana.cli.commands.csar.CsarInfo;
import org.opentosca.toscana.cli.commands.csar.CsarList;
import org.opentosca.toscana.cli.commands.csar.CsarUpload;
import org.opentosca.toscana.cli.commands.csar.ToscanaCsar;
import org.opentosca.toscana.cli.commands.platform.PlatformInfo;
import org.opentosca.toscana.cli.commands.platform.PlatformList;
import org.opentosca.toscana.cli.commands.platform.ToscanaPlatform;
import org.opentosca.toscana.cli.commands.transformation.ToscanaTransformation;
import org.opentosca.toscana.cli.commands.transformation.TransformationDelete;
import org.opentosca.toscana.cli.commands.transformation.TransformationDownload;
import org.opentosca.toscana.cli.commands.transformation.TransformationInfo;
import org.opentosca.toscana.cli.commands.transformation.TransformationInput;
import org.opentosca.toscana.cli.commands.transformation.TransformationList;
import org.opentosca.toscana.cli.commands.transformation.TransformationLogs;
import org.opentosca.toscana.cli.commands.transformation.TransformationStart;
import org.opentosca.toscana.cli.commands.transformation.TransformationStop;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import static picocli.CommandLine.usage;

@Command(name = "help",
    customSynopsis = "@|bold toscana help|@ [@|yellow <command>|@] [@|yellow <subcommand>|@] [@|yellow -mv|@]",
    descriptionHeading = "%nAvailable TOSCAna CLI commands and subcommands:%n",
    description = {"@|bold csar|@                          Show CSAR usage and all subcommands",
        "   @|bold delete|@                     Delete the specified CSAR",
        "   @|bold info|@                       Information about the specified CSAR",
        "   @|bold list|@                       Show all uploaded CSARs",
        "   @|bold upload|@                     Upload the CSAR Archive for Transformation%n",
        "@|bold input|@                         Required Inputs for the specified Transformation%n",
        "@|bold help|@                          The Help page%n",
        "@|bold platform|@                      Show Platform usage and all subcommands",
        "   @|bold info|@                       Information about the specified Platform",
        "   @|bold list|@                       Show all available Platforms for Transformation%n",
        "@|bold status|@                        Show the current State of the System%n",
        "@|bold transformation|@                Show Transformation usage and all subcommands",
        "   @|bold delete|@                     Deletes the specified Transformation",
        "   @|bold download|@                   Downloads the specified Transformation Artifact",
        "   @|bold info|@                       Information about the specific Transformation",
        "   @|bold list|@                       Show all available Transformations for the CSAR",
        "   @|bold logs|@                       Returns logs for the specified Transformation",
        "   @|bold start|@                      Starts a Transformation",
        "   @|bold stop|@                       Stops the specified Transformation"})
public class ToscanaHelp extends AbstractCommand {

    @Parameters(arity = "0..1", paramLabel = "command", description = "Shows help page for every command", hidden = true)
    private String[] helpCommand;

    private Map<String, Object> helpMap;

    /**
     ToscanaHelp shows a help page for every available command/ subcommand
     */
    public ToscanaHelp() {
        helpMap = new HashMap<>();
        helpMap.put("csar", new ToscanaCsar());
        helpMap.put("status", new ToscanaStatus());
        helpMap.put("csar delete", new CsarDelete());
        helpMap.put("csar info", new CsarInfo());
        helpMap.put("csar list", new CsarList());
        helpMap.put("csar upload", new CsarUpload());
        helpMap.put("platform info", new PlatformInfo());
        helpMap.put("platform list", new PlatformList());
        helpMap.put("platform", new ToscanaPlatform());
        helpMap.put("transformation", new ToscanaTransformation());
        helpMap.put("transformation delete", new TransformationDelete());
        helpMap.put("transformation download", new TransformationDownload());
        helpMap.put("transformation info", new TransformationInfo());
        helpMap.put("input", new TransformationInput());
        helpMap.put("transformation list", new TransformationList());
        helpMap.put("transformation logs", new TransformationLogs());
        helpMap.put("transformation start", new TransformationStart());
        helpMap.put("transformation stop", new TransformationStop());
    }

    @Override
    protected String performCall(ApiController ap) {
        return null;
    }

    @Override
    public void run() {
        String help = "";

        if (helpCommand != null) {
            if (helpCommand.length > 1) {
                help += helpCommand[0] + " " + helpCommand[1];
            } else {
                help += helpCommand[0];
            }

            if (helpMap.containsKey(help)) {
                usage(helpMap.get(help), System.out);
            }
        } else {
            usage(this, System.out);
        }
    }
}
