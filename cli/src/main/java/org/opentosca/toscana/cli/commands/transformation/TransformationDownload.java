package org.opentosca.toscana.cli.commands.transformation;

import picocli.CommandLine.Command;

@Command(name = "download",
    description = {"Downloads the specified Transformation Artifact"},
    customSynopsis = {"@|bold toscana transformation download|@ @|yellow -c=<name>|@ @|yellow -p=<name>|@ [@|yellow -mv|@]",
        "   or: @|bold toscana transformation download|@ @|yellow -t=<csar/platform>|@ [@|yellow -mv|@]%n"})
public class TransformationDownload extends AbstractTransformation {

    /**
     shows the link to download an artifact for the specified Transformation
     */
    public TransformationDownload() {
    }
}
