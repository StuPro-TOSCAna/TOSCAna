package org.opentosca.toscana.plugins.kubernetes.docker.dockerfile.builder.commands;

import java.io.PrintWriter;

public class EntrypointCommand extends DockerfileEntry {
    private final String[] commands;

    public EntrypointCommand(String... commands) {
        this.commands = commands;
    }

    @Override
    public void appendToDockerfile(
        PrintWriter out,
        DockerfileEntry previous,
        DockerfileEntry next,
        boolean compact
    ) {
        if (commands.length == 1) {
            out.println("ENTRYPOINT " + commands[0]);
        } else {
            out.print("ENTRYPOINT [");
            for (int i = 0; i < commands.length; i++) {
                String elem = commands[i];
                out.printf("\"%s\"", elem);
                if (i + 1 < commands.length) {
                    out.print(", ");
                }
            }
            out.print("]");
        }
    }
}
