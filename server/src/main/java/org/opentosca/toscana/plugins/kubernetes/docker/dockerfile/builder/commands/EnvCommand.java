package org.opentosca.toscana.plugins.kubernetes.docker.dockerfile.builder.commands;

import java.io.PrintWriter;

public class EnvCommand extends DockerfileEntry {

    private final String variableName;
    private final String value;

    public EnvCommand(String variableName, String value) {
        this.variableName = variableName;
        this.value = value;
    }

    @Override
    public void appendToDockerfile(
        PrintWriter out,
        DockerfileEntry previous,
        DockerfileEntry next,
        boolean compact
    ) {
        out.println("ENV " + variableName + "=" + value);
    }
}
