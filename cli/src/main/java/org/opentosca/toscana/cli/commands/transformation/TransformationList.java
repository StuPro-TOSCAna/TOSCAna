package org.opentosca.toscana.cli.commands.transformation;

import org.opentosca.toscana.cli.ApiController;
import org.opentosca.toscana.cli.commands.AbstractCommand;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "list",
    description = {"Show all available Transformations for specific CSAR"},
    customSynopsis = "@|bold toscana transformation list|@ @|yellow -c=<name>|@ [@|yellow -mv|@]%n")
public class TransformationList extends AbstractCommand implements Runnable {

    @Option(names = {"-c", "--csar"}, required = true, paramLabel = "CSAR", description = "CSAR for Transformation List")
    private String csar;

    /**
     shows a list of available Transformations for the provided CSAR
     */
    public TransformationList() {
    }

    @Override
    public void run() {
        ApiController api = startApi();
        System.out.println(api.listTransformation(csar));
    }
}
