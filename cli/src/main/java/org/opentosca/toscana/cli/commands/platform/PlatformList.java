package org.opentosca.toscana.cli.commands.platform;

import org.opentosca.toscana.cli.ApiController;
import org.opentosca.toscana.cli.commands.AbstractCommand;

import picocli.CommandLine.Command;

@Command(name = "list",
    description = {"Show all available Platforms for Transformation"},
    customSynopsis = "@|bold toscana platform list|@ [@|yellow -mv|@]%n")
public class PlatformList extends AbstractCommand implements Runnable {

    /**
     shows the available platforms, that are ready to start a transformation
     */
    public PlatformList() {
    }

    @Override
    public void run() {
        ApiController api = startApi();
        System.out.println(api.listPlatform());
    }
}
