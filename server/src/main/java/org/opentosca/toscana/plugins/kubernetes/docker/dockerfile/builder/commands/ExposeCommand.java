package org.opentosca.toscana.plugins.kubernetes.docker.dockerfile.builder.commands;

import java.io.PrintWriter;

/**
 Describes the Expose (expose a port) Command for the DockerfileBuilder
 */
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
