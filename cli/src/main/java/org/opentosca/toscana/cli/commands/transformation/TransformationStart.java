package org.opentosca.toscana.cli.commands.transformation;

import java.io.IOException;

import org.opentosca.toscana.cli.ApiController;

import picocli.CommandLine.Command;

@Command(name = "start",
    description = {"Starts a Transformation of the specified CSAR Archive to the specified Platform"},
    customSynopsis = {"@|bold toscana transformation start|@ @|yellow -c=<name>|@ @|yellow -p=<name>|@ [@|yellow -mv|@]",
        "   or: @|bold toscana transformation start|@ @|yellow -t=<csar/platform>|@ [@|yellow -mv|@]%n"})
public class TransformationStart extends AbstractTransformation implements Runnable {

    /**
     starts a Transformation for the specified CSAR and Platform
     */
    public TransformationStart() {
    }

    @Override
    public void run() {
        ApiController api = startApi();

        try {
            final String[] entered = getInput();
            if (entered != null) {
                System.out.println(api.startTransformation(entered[0], entered[1]));
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
