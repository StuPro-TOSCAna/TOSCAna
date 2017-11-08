package org.opentosca.toscana.cli.commands.transformation;

import java.io.IOException;

import org.opentosca.toscana.cli.ApiController;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "logs",
    description = {"Returns logs for the specified Transformation"},
    customSynopsis = {"@|bold toscana transformation logs|@ @|yellow -c=<name>|@ @|yellow -p=<name>|@ @|yellow -s=<number>|@ [@|yellow -mv|@]",
        "   or: @|bold toscana transformation logs|@ @|yellow -t=<csar/platform>|@ @|yellow -s=<number>|@ [@|yellow -mv|@]%n"})
public class TransformationLogs extends AbstractTransformation implements Runnable {

    @Option(names = {"-s", "--start"}, paramLabel = "Logs start", description = "Provide where to start with the Logs")
    private int start = 0;

    /**
     returns all logs available for the Transformation, startpoint for return is set with start
     */
    public TransformationLogs() {
    }

    @Override
    public void run() {
        ApiController api = startApi();

        try {
            final String[] entered = getInput();
            if (entered != null) {
                System.out.println(api.logsTransformation(entered[0], entered[1], start));
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
