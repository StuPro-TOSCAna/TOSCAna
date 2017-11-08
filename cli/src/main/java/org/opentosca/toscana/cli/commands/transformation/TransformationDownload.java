package org.opentosca.toscana.cli.commands.transformation;

import java.io.IOException;

import org.opentosca.toscana.cli.ApiController;

import picocli.CommandLine.Command;

@Command(name = "download",
    description = {"Downloads the specified Transformation Artifact"},
    customSynopsis = {"@|bold toscana transformation download|@ @|yellow -c=<name>|@ @|yellow -p=<name>|@ [@|yellow -mv|@]",
        "   or: @|bold toscana transformation download|@ @|yellow -t=<csar/platform>|@ [@|yellow -mv|@]%n"})
public class TransformationDownload extends AbstractTransformation implements Runnable {

    /**
     shows the link to download an artifact for the specified Transformation
     */
    public TransformationDownload() {
    }

    @Override
    public void run() {
        ApiController api = startApi();

        try {
            final String[] entered = getInput();
            if (entered != null) {
                System.out.println(api.downloadTransformation(entered[0], entered[1]));
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
