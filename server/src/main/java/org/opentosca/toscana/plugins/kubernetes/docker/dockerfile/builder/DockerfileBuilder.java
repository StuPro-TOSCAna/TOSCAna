package org.opentosca.toscana.plugins.kubernetes.docker.dockerfile.builder;

import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.plugins.kubernetes.docker.dockerfile.builder.commands.CopyCommand;
import org.opentosca.toscana.plugins.kubernetes.docker.dockerfile.builder.commands.DockerfileEntry;
import org.opentosca.toscana.plugins.kubernetes.docker.dockerfile.builder.commands.EntrypointCommand;
import org.opentosca.toscana.plugins.kubernetes.docker.dockerfile.builder.commands.EnvCommand;
import org.opentosca.toscana.plugins.kubernetes.docker.dockerfile.builder.commands.ExposeCommand;
import org.opentosca.toscana.plugins.kubernetes.docker.dockerfile.builder.commands.FromCommand;
import org.opentosca.toscana.plugins.kubernetes.docker.dockerfile.builder.commands.RunCommand;
import org.opentosca.toscana.plugins.kubernetes.docker.dockerfile.builder.commands.VolumeCommand;
import org.opentosca.toscana.plugins.kubernetes.docker.dockerfile.builder.commands.WorkdirCommand;

/**
 This class allows the in code creation of dockerfiles.
 */
public class DockerfileBuilder {

    private final String baseImage;
    private final String workingDir;
    private final PluginFileAccess fileAccess;

    private final List<DockerfileEntry> entries = new ArrayList<>();

    private boolean compress = false;

    /**
     Create a new instance of the DockerfileBuilder

     @param baseImage  the "url" to the base image to use in this dockerfile
     @param workingDir the working directory (relative to transformation root)
     @param fileAccess the fileAccess to allow the writing of files
     */
    public DockerfileBuilder(
        String baseImage,
        String workingDir,
        PluginFileAccess fileAccess
    ) {
        this.baseImage = baseImage;
        //Strip of trailing / if it exists
        if (workingDir.endsWith("/")) {
            workingDir = workingDir.substring(0, workingDir.length() - 1);
        }
        this.workingDir = workingDir;
        this.fileAccess = fileAccess;

        entries.add(new FromCommand(baseImage));
    }

    public DockerfileBuilder copyFromCsar(String inputPath, String name, String dfPath) throws IOException {
        String dir = workingDir + "/" + name;
        fileAccess.createDirectories(dir);
        fileAccess.copy(inputPath, dir);
        this.entries.add(new CopyCommand(name, dfPath));
        return this;
    }

    public DockerfileBuilder copyFormWokringDir(String inputPath, String dfPath) {
        entries.add(new CopyCommand(inputPath, dfPath));
        return this;
    }

    /**
     Adds a <code>RUN</code> command to the dockerfile. This is considered just like a regular bash script.

     @param command the command to add to the dockerfile
     @return the object itself to allow chaining (not needed though).
     */
    public DockerfileBuilder run(String command) {
        entries.add(new RunCommand(command));
        return this;
    }

    /**
     Adds a <code>ENTRYPOINT</code> command to the Dockerfile

     @param args If only one command is given <code>ENTRYPOINT <args[0]></code> will be added to the dockerfile otherwise
     <code>ENTRYPOINT ["<args[0]>","<args[1]>"...]</code> will be added otherwise
     @return the object itself to allow chaining (not needed though).
     */
    public DockerfileBuilder entrypoint(String... args) {
        entries.add(new EntrypointCommand(args));
        return this;
    }

    /**
     Adds a <code>EXPOSE</code> command for the given port to the dockerfile

     @param port The port to expose
     @return the object itself to allow chaining (not needed though).
     */
    public DockerfileBuilder expose(int port) {
        entries.add(new ExposeCommand(port));
        return this;
    }

    /**
     Adds a <code>VOLUME</code> command for the given path to the dockerfile

     @param path the path at wich the volume should be created within the Container Image
     @return the object itself to allow chaining (not needed though).
     */
    public DockerfileBuilder volume(String path) {
        entries.add(new VolumeCommand(path));
        return this;
    }

    /**
     Adds a <code>WORKDIR</code> command for the given path to the dockerfile

     @param directory the path at wich the working directory should be set within the Container Image
     @return the object itself to allow chaining (not needed though).
     */
    public DockerfileBuilder workdir(String directory) {
        entries.add(new WorkdirCommand(directory));
        return this;
    }

    /**
     Adds a <code>ENV</code> command to the dockerfile

     @param variableName The name of the environment variable
     @param value        the corresponding value
     @return the object itself to allow chaining (not needed though).
     */
    public DockerfileBuilder env(String variableName, String value) {
        entries.add(new EnvCommand(variableName, value));
        return this;
    }

    /**
     Enables the compression of run commands to one run command. this means that they get linked using <code>&&</code>
     <p>
     <b>Warning:</b> Using this command does not ensure the same behaviour as two sperated run commands.
     (the current directory ist not reset to the current WORKDIR)

     @return the object itself to allow chaining (not needed though).
     */
    public DockerfileBuilder compressRunCommands() {
        compress = true;
        return this;
    }

    /**
     Writes the dockerfile to the path defined in the constructor
     */
    public void build() throws IOException {
        BufferedWriter w = fileAccess.access(workingDir + "/Dockerfile");
        writeTo(w);
        w.close();
    }

    private void writeTo(Writer out) {
        PrintWriter pw = new PrintWriter(out);
        for (int i = 0; i < entries.size(); i++) {
            DockerfileEntry prev = i > 0 && entries.size() > 1 ? entries.get(i - 1) : null;
            DockerfileEntry current = entries.get(i);
            DockerfileEntry next = i + 1 < entries.size() ? entries.get(i + 1) : null;

            current.appendToDockerfile(pw, prev, next, compress);
        }
    }

    /**
     Builds the dockerfile to a string
     */
    public String buildToString() {
        CharArrayWriter out = new CharArrayWriter();
        writeTo(out);
        return new String(out.toCharArray());
    }
}
