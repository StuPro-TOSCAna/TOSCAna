package org.opentosca.toscana.plugins.kubernetes.docker.dockerfile.builder.commands;

import java.io.PrintWriter;

public class ExposeCommand extends DockerfileEntry {
    private final int port;

    public ExposeCommand(int port) {
        this.port = port;
    }

    @Override
    public void appendToDockerfile(
        PrintWriter out,
        DockerfileEntry previous,
        DockerfileEntry next,
        boolean compact
    ) {
        out.println("EXPOSE " + port);
    }
}
