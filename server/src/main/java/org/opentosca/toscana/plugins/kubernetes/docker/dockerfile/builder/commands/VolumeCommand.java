package org.opentosca.toscana.plugins.kubernetes.docker.dockerfile.builder.commands;

import java.io.PrintWriter;

/**
 Describes the Volume (Set a Volume Directory) Command for the DockerfileBuilder
 */
public class VolumeCommand extends DockerfileEntry {
    private final String path;

    public VolumeCommand(String path) {
        this.path = path;
    }

    @Override
    public void appendToDockerfile(
        PrintWriter out,
        DockerfileEntry previous,
        DockerfileEntry next,
        boolean compact
    ) {
        out.println("VOLUME " + path);
    }
}
