package org.opentosca.toscana.cli.commands.transformation;

import org.opentosca.toscana.cli.ApiController;

import picocli.CommandLine.Command;

@Command(name = "delete",
    description = {"Deletes the specified Transformation"},
    customSynopsis = {"@|bold toscana transformation delete|@ @|yellow -c=<name>|@ @|yellow -p=<name>|@ [@|yellow -mv|@]",
        "   or: @|bold toscana transformation delete|@ @|yellow -t=<csar/platform>|@ [@|yellow -mv|@]%n"})
public class TransformationDelete extends AbstractTransformation {

    /**
     deletes the specified Transformation from the Transformator
     */
    public TransformationDelete() {

    }

    @Override
    protected String performCall(ApiController ap, String[] ent) {
        return ap.deleteTransformation(ent[0], ent[1]);
    }
}
