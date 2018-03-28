package org.opentosca.toscana.plugins.kubernetes.docker.dockerfile.builder.commands;

import java.io.PrintWriter;

/**
 This is a Abstract class to describe the commands that can be created using the Dockerfile builder
 */
public abstract class DockerfileEntry {

    /**
     A Command should write the corresponding command to the given PrintWriter using println() for example like that
     <code>out.println("RUN echo \"Hello\");</code>

     @param out      The PrintWriter to write to
     @param previous The Previous Dockerfile Entry
     @param next     The next Dockerfile entry
     @param compact  this value is true if RUN commands should be compressed to one command using chain execution with <code>&&</code>
     */
    public abstract void appendToDockerfile(
        PrintWriter out,
        DockerfileEntry previous,
        DockerfileEntry next,
        boolean compact
    );
}
