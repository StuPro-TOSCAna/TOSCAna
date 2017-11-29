package org.opentosca.toscana.cli.commands.csar;

import org.opentosca.toscana.cli.ApiController;
import org.opentosca.toscana.cli.commands.AbstractCommand;
import org.opentosca.toscana.cli.commands.Constants;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "delete",
    description = {"Delete the specified CSAR"},
    customSynopsis = "@|bold toscana csar delete|@ @|yellow -c=<name>|@ [@|yellow -mv|@]%n")
public class CsarDelete extends AbstractCommand {

    @Option(names = {"-c", "--csar"}, required = true, paramLabel = Constants.PARAM_CSAR, description = "the CSAR to delete")
    private String csar;

    /**
     Delete's the specified CSAR from the Transformator
     */
    public CsarDelete() {
    }

    @Override
    protected String performCall(ApiController ap) {
        return ap.deleteCsar(csar);
    }
}
