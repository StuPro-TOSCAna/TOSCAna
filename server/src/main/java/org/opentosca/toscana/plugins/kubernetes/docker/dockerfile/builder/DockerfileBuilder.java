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
 Allows the building of a dockerfile.
 */
public class DockerfileBuilder {

    private final String baseImage;
    private final String workingDir;
    private final PluginFileAccess fileAccess;

    private final List<DockerfileEntry> entries = new ArrayList<>();

    private boolean compress = false;
    private List<CopyCommand> copyCommands;
    private List<EnvCommand> envCommands;
    private int lastWorkdirCommand = 0;

    /**
     Create a new instance of the DockerfileBuilder

     @param baseImage  the "url" to the base image to use in this dockerfile
     @param workingDir the working directory (relative to transformation root)
     @param fileAccess the fileAccess to allow the writing of files
     */
    public DockerfileBuilder(String baseImage, String workingDir, PluginFileAccess fileAccess) {
        copyCommands = new ArrayList<>();
        envCommands = new ArrayList<>();
        this.baseImage = baseImage;
        //Strip off trailing / if it exists
        if (workingDir.endsWith("/")) {
            workingDir = workingDir.substring(0, workingDir.length() - 1);
        }
        this.workingDir = workingDir;
        this.fileAccess = fileAccess;

        entries.add(new FromCommand(baseImage));
    }

    /**
     Copies a File or directory from the Csar Directory to the Working Directory of the dockerfile builder.
     <p>
     After that the file gets added to the Container image using the <code>COPY</code> command.

     @param inputPath   The relative input path from within the CSAR (can be a file or a directory
     @param workdirName The name of the folder in witch the contents should be stored
     @param fileName    the path at witch the files should be inserted
     in the container image (use <code>.</code> to insert at the working directory)
     @return the object itself to allow chaining (not needed though).
     @throws IOException gets thrown if the coping fails (passed through from the
     <code>copy</code> method of the PluginFileAccess)
     */
    public DockerfileBuilder copyFromCsar(String inputPath, String workdirName, String fileName) throws IOException {
        String dir = workingDir + "/" + workdirName;
        fileAccess.createDirectories(dir);
        fileAccess.copy(inputPath, dir + "/" + fileName);
        copyCommands.add(new CopyCommand((workdirName.isEmpty() ? "" : workdirName + "/") + fileName, fileName));
        return this;
    }

    /**
     Adds a <code>COPY</code> command to the dockerfile that
     should copy the input path (should be relative to the dockerfile working directory)
     to the specified container path

     @param inputPath     the input path to copy (relative to dockerfile workingdir)
     @param containerPath the path within the container (<code>.</code> for the current directory)
     @return the object itself to allow chaining (not needed though).
     */
    public DockerfileBuilder copyFromWorkingDir(String inputPath, String containerPath) {
        copyCommands.add(new CopyCommand(inputPath, containerPath));
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

     @param args If only one command is given <code>ENTRYPOINT <args[0]></code> will
     be added to the dockerfile otherwise
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
        addCopyCommandsToEntries();
        entries.add(new WorkdirCommand(directory));
        lastWorkdirCommand = entries.size() - 1;
        return this;
    }

    /**
     Adds all collected copy commands at the beginning of the entries list
     or after a workdir command
     */
    private void addCopyCommandsToEntries() {
        if (entries.size() <= 1) {
            return;
        }
        int index = (lastWorkdirCommand == 0) ? 1 : lastWorkdirCommand;
        index++;
        entries.addAll(index, copyCommands);
        copyCommands.clear();
    }

    /**
     Adds a <code>ENV</code> command to the dockerfile

     @param variableName The name of the environment variable
     @param value        the corresponding value
     @return the object itself to allow chaining (not needed though).
     */
    public DockerfileBuilder env(String variableName, String value) {
        envCommands.add(new EnvCommand(variableName, value));
        return this;
    }

    /**
     Enables the compression of several run commands to one single run command.
     This means that they get linked using <code>&&</code>
     <p>
     <b>Warning:</b> Using this command does not ensure the same behaviour as two separated run commands.
     (the current directory ist not reset to the current WorkDir)

     @return the caller
     */
    public DockerfileBuilder compressRunCommands() {
        compress = true;
        return this;
    }

    /**
     Writes the dockerfile to the path defined in the constructor
     */
    public void write() throws IOException {
        BufferedWriter w = fileAccess.access(workingDir + "/Dockerfile");
        writeTo(w);
        w.close();
    }

    private void writeTo(Writer out) {
        addCopyCommandsToEntries();
        addEnvCommandsToEntries();
        PrintWriter pw = new PrintWriter(out);
        for (int i = 0; i < entries.size(); i++) {
            DockerfileEntry prev = i > 0 && entries.size() > 1 ? entries.get(i - 1) : null;
            DockerfileEntry current = entries.get(i);
            DockerfileEntry next = i + 1 < entries.size() ? entries.get(i + 1) : null;

            current.appendToDockerfile(pw, prev, next, compress);
        }
    }

    /**
     Add all EnvCommands at the beginning of the entries list
     */
    private void addEnvCommandsToEntries() {
        entries.addAll(1, envCommands);
        envCommands.clear();
    }

    /**
     Builds the dockerfile to a string
     */
    @Override
    public String toString() {
        CharArrayWriter out = new CharArrayWriter();
        writeTo(out);
        return new String(out.toCharArray());
    }
}
