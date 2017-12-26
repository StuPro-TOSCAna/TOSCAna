package org.opentosca.toscana.cli.commands.transformation;

import org.opentosca.toscana.cli.ApiController;

import picocli.CommandLine.Command;

@Command(name = "start",
    description = {"Starts a Transformation of the specified CSAR Archive to the specified Platform"},
    customSynopsis = {"@|bold toscana transformation start|@ @|yellow -c=<name>|@ @|yellow -p=<name>|@ [@|yellow -mv|@]",
        "   or: @|bold toscana transformation start|@ @|yellow -t=<csar/platform>|@ [@|yellow -mv|@]%n"})
public class TransformationStart extends AbstractTransformation {

    /**
     starts a Transformation for the specified CSAR and Platform
     */
    public TransformationStart() {

    }

    @Override
    protected String performCall(ApiController ap, String[] ent) {
        return ap.startTransformation(ent[0], ent[1]);
    }
}
