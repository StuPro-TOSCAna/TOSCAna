package org.opentosca.toscana.cli.commands.transformation;

import java.io.File;

import org.opentosca.toscana.cli.ApiController;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "download",
    description = {"Downloads the specified Transformation Artifact"},
    customSynopsis = {"@|bold toscana transformation download|@ @|yellow -c=<name>|@ @|yellow -p=<name>|@ [@|yellow -mv|@]",
        "   or: @|bold toscana transformation download|@ @|yellow -t=<csar/platform>|@ [@|yellow -mv|@]%n"})
public class TransformationDownload extends AbstractTransformation {

    @Option(names = {"-o", "--output"}, paramLabel = "Output File", description = "Output File Destination for Artifact Download")
    private File outputFile;

    /**
     shows the link to download an artifact for the specified Transformation
     */
    public TransformationDownload() {
    }

    @Override
    protected String performCall(ApiController ap, String[] ent) {
        if (outputFile != null) {
            return ap.downloadTransformationStream(ent[0], ent[1], outputFile);
        } else {
            return ap.downloadTransformationUrl(ent[0], ent[1]);
        }
    }
}
