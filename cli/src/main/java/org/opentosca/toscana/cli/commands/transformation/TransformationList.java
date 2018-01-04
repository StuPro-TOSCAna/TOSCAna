package org.opentosca.toscana.cli.commands.transformation;

import org.opentosca.toscana.cli.ApiController;

import picocli.CommandLine.Command;

@Command(name = "list",
    description = {"Show all available Transformations for specific CSAR"},
    customSynopsis = "@|bold toscana transformation list|@ @|yellow -c=<name>|@ [@|yellow -mv|@]%n")
public class TransformationList extends AbstractTransformation {

    /**
     shows a list of available Transformations for the provided CSAR
     */
    public TransformationList() {

    }

    @Override
    protected String performCall(ApiController ap, String[] ent) {
        return ap.listTransformation(ent[0]);
    }
}
