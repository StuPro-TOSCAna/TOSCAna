package org.opentosca.toscana.cli.commands.platform;

import org.opentosca.toscana.cli.ApiController;
import org.opentosca.toscana.cli.commands.AbstractCommand;

import picocli.CommandLine.Command;

@Command(name = "list",
    description = {"Show all available Platforms for Transformation"},
    customSynopsis = "@|bold toscana platform list|@ [@|yellow -mv|@]%n")
public class PlatformList extends AbstractCommand {

    /**
     shows the available platforms, that are ready to start a transformation
     */
    public PlatformList() {

    }

    @Override
    protected String performCall(ApiController ap) {
        return api.listPlatform();
    }
}
