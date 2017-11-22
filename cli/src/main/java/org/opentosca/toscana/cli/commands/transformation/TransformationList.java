package org.opentosca.toscana.cli.commands.transformation;

import java.io.IOException;

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
    public void run() {
        try {
            System.out.println(callTransformationList());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
