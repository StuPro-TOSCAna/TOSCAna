package org.opentosca.toscana.cli.commands.csar;

import org.opentosca.toscana.cli.ApiController;
import org.opentosca.toscana.cli.commands.AbstractCommand;
import org.opentosca.toscana.cli.commands.Constants;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "info",
    description = {"Information about the specified CSAR"},
    customSynopsis = "@|bold toscana csar info|@ @|yellow -c=<name>|@ [@|yellow -mv|@]%n")
public class CsarInfo extends AbstractCommand {

    @Option(names = {"-c", "--csar"}, required = true, paramLabel = Constants.PARAM_CSAR, description = "Information about the CSAR")
    private String csar;

    /**
     Displays Information for the specific CSAR
     */
    public CsarInfo() {
    }

    @Override
    protected String performCall(ApiController ap) {
        return ap.infoCsar(csar);
    }
}
