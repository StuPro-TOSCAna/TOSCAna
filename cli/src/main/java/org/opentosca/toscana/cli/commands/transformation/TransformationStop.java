package org.opentosca.toscana.cli.commands.transformation;

import java.io.IOException;

import org.opentosca.toscana.cli.ApiController;

import picocli.CommandLine.Command;

@Command(name = "stop",
    description = {"Stops the specified Transformation"},
    customSynopsis = {"@|bold toscana transformation stop|@ @|yellow -c=<name>|@ @|yellow -p=<name>|@ [@|yellow -mv|@]",
        "   or: @|bold toscana transformation stop|@ @|yellow -t=<csar/platform>|@ [@|yellow -mv|@]%n"})
public class TransformationStop extends AbstractTransformation implements Runnable {

    /**
     stops the specified currently running Transformation
     */
    public TransformationStop() {
    }

    @Override
    public void run() {
        ApiController api = startApi();

        try {
            final String[] entered = getInput();
            if (entered != null) {
                System.out.println(api.stopTransformation(entered[0], entered[1]));
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
