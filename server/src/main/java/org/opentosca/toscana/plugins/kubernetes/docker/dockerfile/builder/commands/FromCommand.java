package org.opentosca.toscana.plugins.kubernetes.docker.dockerfile.builder.commands;

import java.io.PrintWriter;

/**
 Describes the From (Set Parent image) Command for the DockerfileBuilder
 */
public class FromCommand extends DockerfileEntry {

    private final String baseImage;

    public FromCommand(String baseImage) {
        this.baseImage = baseImage;
    }

    @Override
    public void appendToDockerfile(
        PrintWriter out,
        DockerfileEntry previous,
        DockerfileEntry next,
        boolean compact
    ) {
        out.println("FROM " + baseImage);
    }
}
