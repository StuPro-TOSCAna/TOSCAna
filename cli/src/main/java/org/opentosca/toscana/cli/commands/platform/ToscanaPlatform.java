package org.opentosca.toscana.cli.commands.platform;

import org.opentosca.toscana.cli.ApiController;
import org.opentosca.toscana.cli.commands.AbstractCommand;

import picocli.CommandLine.Command;

import static picocli.CommandLine.usage;

@Command(name = "platform",
    customSynopsis = "@|bold toscana platform|@ [@|yellow <subcommand>|@] [@|yellow -mv|@]%n",
    commandListHeading = "%nAvailable Subcommands are:%n",
    subcommands = {PlatformInfo.class,
        PlatformList.class})
public class ToscanaPlatform extends AbstractCommand {

    /**
     Show's the usage of the platform command and all available subcommands
     */
    public ToscanaPlatform() {
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
