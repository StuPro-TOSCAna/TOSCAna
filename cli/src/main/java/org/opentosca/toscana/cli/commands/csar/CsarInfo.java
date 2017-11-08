package org.opentosca.toscana.cli.commands.csar;

import java.io.IOException;

import org.opentosca.toscana.cli.ApiController;
import org.opentosca.toscana.cli.commands.AbstractCommand;
import org.opentosca.toscana.cli.commands.Constants;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "info",
    description = {"Information about the specified CSAR"},
    customSynopsis = "@|bold toscana csar info|@ @|yellow -c=<name>|@ [@|yellow -mv|@]%n")
public class CsarInfo extends AbstractCommand implements Runnable {

    @Option(names = {"-c", "--csar"}, required = true, paramLabel = Constants.PARAM_CSAR, description = "Information about the CSAR")
    private String csar;

    /**
     Displays Information for the specific CSAR
     */
    public CsarInfo() {
    }

    @Override
    public void run() {
        ApiController api = startApi();

        try {
            System.out.println(api.infoCsar(csar));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
