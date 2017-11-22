package org.opentosca.toscana.cli.commands.transformation;

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
}
