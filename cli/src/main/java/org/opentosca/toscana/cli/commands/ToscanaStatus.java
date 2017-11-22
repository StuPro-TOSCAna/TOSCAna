package org.opentosca.toscana.cli.commands;

import picocli.CommandLine.Command;

@Command(name = "status",
    description = {"Show the current State of the System"},
    customSynopsis = "@|bold toscana status|@ [@|yellow -mv|@]%n")
public class ToscanaStatus extends AbstractCommand {

    /**
     Get's called if the current state of the system is requested
     */
    public ToscanaStatus() {
    }

    @Override
    public void run() {
        System.out.println(getApi().showStatus());
    }
}
