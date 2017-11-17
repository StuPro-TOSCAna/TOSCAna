package org.opentosca.toscana.cli.commands.transformation;

import java.io.IOException;

import org.opentosca.toscana.cli.ApiController;

import picocli.CommandLine.Command;

@Command(name = "info",
    description = {"Information about the specific Transformation"},
    customSynopsis = {"@|bold toscana transformation info|@ @|yellow -c=<name>|@ @|yellow -p=<name>|@ [@|yellow -mv|@]",
        "   or: @|bold toscana transformation info|@ @|yellow -t=<csar/platform>|@ [@|yellow -mv|@]%n"})
public class TransformationInfo extends AbstractTransformation implements Runnable {

    /**
     shows available Information about a specified Transformations
     */
    public TransformationInfo() {
    }

    @Override
    public void run() {
        ApiController api = startApi();

        try {
            final String[] entered = getInput();
            if (entered != null) {
                System.out.println(api.infoTransformation(entered[0], entered[1]));
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
