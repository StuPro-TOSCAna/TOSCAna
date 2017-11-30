package org.opentosca.toscana.plugins.kubernetes.docker.dockerfile.builder.commands;

import java.io.PrintWriter;

public class CopyCommand extends DockerfileEntry {

    private final String inputFolder;
    private final String imagePath;

    public CopyCommand(String inputFolder, String imagePath) {
        this.inputFolder = inputFolder;
        this.imagePath = imagePath;
    }

    @Override
    public void appendToDockerfile(
        PrintWriter out,
        DockerfileEntry previous,
        DockerfileEntry next,
        boolean compact
    ) {
        out.println("COPY " + inputFolder + " " + imagePath);
    }
}
