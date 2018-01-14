package org.opentosca.toscana.cli.commands.platform;

import org.opentosca.toscana.cli.ApiController;
import org.opentosca.toscana.cli.commands.AbstractCommand;
import org.opentosca.toscana.cli.commands.Constants;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "info",
    description = {"Information about the specified Platform"},
    customSynopsis = "@|bold toscana platform info|@ @|yellow -p=<name>|@ [@|yellow -mv|@]%n",
    optionListHeading = "%nOptions:%n")
public class PlatformInfo extends AbstractCommand {

    @Option(names = {"-p", "--platform"}, required = true, paramLabel = Constants.PARAM_PLATFORM, description = "Information about the Platform")
    private String platformID;

    /**
     show's information about the wanted platform
     */
    public PlatformInfo() {

    }

    @Override
    protected String performCall(ApiController ap) {
        return ap.infoPlatform(platformID);
    }
}
