package org.opentosca.toscana.plugins.kubernetes.docker.dockerfile.builder.commands;

import java.io.PrintWriter;

public class WorkdirCommand extends DockerfileEntry {
    private final String path;

    public WorkdirCommand(String path) {
        this.path = path;
    }

    @Override
    public void appendToDockerfile(
        PrintWriter out,
        DockerfileEntry previous,
        DockerfileEntry next,
        boolean compact
    ) {
        out.println("WORKDIR " + path);
    }
}
