package org.opentosca.toscana.cli.commands.transformation;

import org.opentosca.toscana.cli.ApiController;

import picocli.CommandLine.Command;

@Command(name = "stop",
    description = {"Stops the specified Transformation"},
    customSynopsis = {"@|bold toscana transformation stop|@ @|yellow -c=<name>|@ @|yellow -p=<name>|@ [@|yellow -mv|@]",
        "   or: @|bold toscana transformation stop|@ @|yellow -t=<csar/platform>|@ [@|yellow -mv|@]%n"})
public class TransformationStop extends AbstractTransformation {

    /**
     stops the specified currently running Transformation
     */
    public TransformationStop() {

    }

    @Override
    protected String performCall(ApiController ap, String[] ent) {
        return ap.stopTransformation(ent[0], ent[1]);
    }
}
