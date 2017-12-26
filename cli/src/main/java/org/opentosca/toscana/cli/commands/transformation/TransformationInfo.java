package org.opentosca.toscana.cli.commands.transformation;

import org.opentosca.toscana.cli.ApiController;

import picocli.CommandLine.Command;

@Command(name = "info",
    description = {"Information about the specific Transformation"},
    customSynopsis = {"@|bold toscana transformation info|@ @|yellow -c=<name>|@ @|yellow -p=<name>|@ [@|yellow -mv|@]",
        "   or: @|bold toscana transformation info|@ @|yellow -t=<csar/platform>|@ [@|yellow -mv|@]%n"})
public class TransformationInfo extends AbstractTransformation {

    /**
     shows available Information about a specified Transformations
     */
    public TransformationInfo() {

    }

    @Override
    protected String performCall(ApiController ap, String[] ent) {
        return ap.infoTransformation(ent[0], ent[1]);
    }
}
