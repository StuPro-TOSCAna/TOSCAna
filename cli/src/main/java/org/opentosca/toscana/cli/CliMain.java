package org.opentosca.toscana.cli;

import org.opentosca.toscana.cli.commands.AbstractCommand;
import org.opentosca.toscana.cli.commands.ToscanaHelp;
import org.opentosca.toscana.cli.commands.ToscanaStatus;
import org.opentosca.toscana.cli.commands.csar.ToscanaCsar;
import org.opentosca.toscana.cli.commands.platform.ToscanaPlatform;
import org.opentosca.toscana.cli.commands.transformation.ToscanaTransformation;
import org.opentosca.toscana.cli.commands.transformation.TransformationInput;

import org.fusesource.jansi.AnsiConsole;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import static picocli.CommandLine.usage;

@Command(name = "toscana",
    header = "@|bold,green TOSCAna Command Line Interface%n|@",
    customSynopsis = "@|bold toscana|@ [@|yellow <command>|@] [@|yellow <subcommand>|@] [@|yellow -mv|@]",
    commandListHeading = "%nMost commonly used TOSCAna commands are:%n",
    footer = {"%nSee 'toscana help [@|yellow <command>|@]' or 'toscana help [@|yellow <command>|@] [@|yellow <subcommand>|@]' to read about a specific command or subcommand.",
        "You can set the API URL which is used in the CLI, in the cli.properties file under the Toscana folder"},
    subcommands = {ToscanaCsar.class,
        ToscanaHelp.class,
        TransformationInput.class,
        ToscanaPlatform.class,
        ToscanaStatus.class,
        ToscanaTransformation.class})
public class CliMain extends AbstractCommand {
    private ApiController apiController;

    public CliMain() {
    }

    public void setApiController(ApiController apiController) {
        this.apiController = apiController;
    }

    public void main(String[] args) {

        //Activate ANSI on Windows
        AnsiConsole.systemInstall();
        System.setProperty("picocli.ansi", "true");

        //System Property gets set here, because picocli.trace must be set before CommandLine starts
        String input = String.join("", args);
        if (input.contains("-m")) {
            System.setProperty("picocli.trace", "DEBUG");
        } else if (input.contains("-v")) {
            System.setProperty("picocli.trace", "INFO");
        }

        CommandLine commandLine = new CommandLine(new CliMain(), new CliPropertiesFactory(apiController));
        commandLine.parseWithHandler(new CommandLine.RunLast(), System.err, args);
        AnsiConsole.systemUninstall();
    }

    @Override
    protected String performCall(ApiController ap) {
        return null;
    }

    @Override
    public void run() {
        usage(this, System.out);
    }
}

