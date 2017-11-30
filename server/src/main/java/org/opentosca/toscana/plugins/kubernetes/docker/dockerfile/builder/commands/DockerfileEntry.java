package org.opentosca.toscana.plugins.kubernetes.docker.dockerfile.builder.commands;

import java.io.PrintWriter;

public abstract class DockerfileEntry {
    public abstract void appendToDockerfile(
        PrintWriter out,
        DockerfileEntry previous,
        DockerfileEntry next,
        boolean compact
    );
}
