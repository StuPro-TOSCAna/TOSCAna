package org.opentosca.toscana.cli.commands.transformation;

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
}
