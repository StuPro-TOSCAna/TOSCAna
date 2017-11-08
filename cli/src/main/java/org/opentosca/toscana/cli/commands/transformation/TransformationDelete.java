package org.opentosca.toscana.cli.commands.transformation;

import java.io.IOException;

import org.opentosca.toscana.cli.ApiController;

import picocli.CommandLine.Command;

@Command(name = "delete",
    description = {"Deletes the specified Transformation"},
    customSynopsis = {"@|bold toscana transformation delete|@ @|yellow -c=<name>|@ @|yellow -p=<name>|@ [@|yellow -mv|@]",
        "   or: @|bold toscana transformation delete|@ @|yellow -t=<csar/platform>|@ [@|yellow -mv|@]%n"})
public class TransformationDelete extends AbstractTransformation implements Runnable {

    /**
     deletes the specified Transformation from the Transformator
     */
    public TransformationDelete() {
    }

    @Override
    public void run() {
        ApiController api = startApi();

        try {
            final String[] entered = getInput();
            if (entered != null) {
                System.out.println(api.deleteTransformation(entered[0], entered[1]));
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
