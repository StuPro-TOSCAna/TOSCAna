package org.opentosca.toscana.plugins.kubernetes.docker.dockerfile.builder.commands;

import java.io.PrintWriter;

public class RunCommand extends DockerfileEntry {
    private final String command;

    public RunCommand(String command) {
        this.command = command;
    }

    @Override
    public void appendToDockerfile(
        PrintWriter out,
        DockerfileEntry previous,
        DockerfileEntry next,
        boolean compact
    ) {
        if (!compact) {
            out.println("RUN " + command);
        } else {
            if (!(previous instanceof RunCommand)) {
                out.print("RUN " + command);
            } else {
                out.print("    " + command);
            }

            if (next instanceof RunCommand) {
                out.println(" && \\");
            } else {
                out.println();
            }
        }
    }
}
