package org.opentosca.toscana.cli.commands.transformation;

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
}
