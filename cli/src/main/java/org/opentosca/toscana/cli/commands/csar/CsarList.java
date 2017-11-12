package org.opentosca.toscana.cli.commands.csar;

import java.io.IOException;

import org.opentosca.toscana.cli.ApiController;
import org.opentosca.toscana.cli.commands.AbstractCommand;

import picocli.CommandLine.Command;

@Command(name = "list",
    description = {"Show all uploaded CSARs"},
    customSynopsis = "@|bold toscana csar list|@ [@|yellow -mv|@]%n")
public class CsarList extends AbstractCommand implements Runnable {

    /**
     Get's called if the available CSARs should be printed
     */
    public CsarList() {
    }

    @Override
    public void run() {
        ApiController api = startApi();

        try {
            System.out.println(api.listCsar());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
