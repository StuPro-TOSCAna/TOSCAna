package org.opentosca.toscana.cli.commands;

import org.opentosca.toscana.cli.ApiController;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 Abstract class to provide often used Options and the ApiController initialization
 */
@Command(//parameterListHeading = "%nParameters:%n",
    optionListHeading = "%nOptions:%n",
    commandListHeading = "%nCommands:%n")
public abstract class AbstractCommand {

    @Option(names = {"-v", "--verbose"}, description = "Enable Info Level Process Output")
    private boolean showVerbose;

    @Option(names = {"-m", "--moreverbose"}, description = "Enable Debug Level Process Output")
    private boolean showMVerbose;

    public ApiController startApi() {
        ApiController api;
        if (showMVerbose) {
            api = new ApiController(true, false);
            return api;
        } else if (showVerbose) {
            api = new ApiController(false, true);
            return api;
        } else {
            api = new ApiController(false, false);
            return api;
        }
    }
}
