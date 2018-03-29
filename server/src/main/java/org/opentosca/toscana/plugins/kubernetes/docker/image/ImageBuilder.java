package org.opentosca.toscana.plugins.kubernetes.docker.image;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.transformation.TransformationContext;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.ProgressHandler;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.messages.ProgressMessage;
import org.slf4j.Logger;

/**
 Implements the building functionality for Docker images (it requires a running Docker Daemon and the proper privileges)
 <p>
 Storage functionality such as Pushing or Exporting gets implemented in the corresponding child classes
 */
public abstract class ImageBuilder implements ProgressHandler {

    protected final Logger logger;
    protected final String dockerWorkDir;
    protected final PluginFileAccess access;

    private final String tag;

    /**
     @param tag           the tag ("name") that the resulting image should have (should never Include any kind of registry URL, Username or Repository)
     @param dockerWorkDir The working directory (relative to transformation root) that contains the dockerfile
     @param context       The TransformationContext used to retrieve the logger and the PluginFileAccess
     */
    public ImageBuilder(String tag, String dockerWorkDir, TransformationContext context) {
        this.tag = tag;
        this.dockerWorkDir = dockerWorkDir;
        this.access = context.getPluginFileAccess();
        this.logger = context.getLogger(getClass());
    }

    /**
     This Command Builds the DockerImage using the Dockerfile in the (previously specified) dockerWorkDir

     @throws Exception gets thrown if something during the Build Procedure fails
     */
    public void buildImage() throws Exception {
        DockerClient client = getDockerClient();

        Path abs = Paths.get(access.getAbsolutePath(dockerWorkDir));
        // Build the image
        String result = client.build(abs, getTag(), this);
        logger.info("Image build was successful. Image ID: {}", result);
    }

    /**
     Returns the tag of the given image.
     (This method should be overridden if the Storing mechanism requires different tags, like registry urls in the tags...)
     */
    public String getTag() {
        return tag;
    }

    /**
     This Method is intended to store the image somehow. (for example by pushing to a registry)

     @throws Exception gets thrown if something fails during the store process
     */
    public abstract void storeImage() throws Exception;

    /**
     Cleans the local Docker Daemon (Removes the image localy)

     @throws Exception gets thrown if something fails during the cleanup process
     */
    public void cleanup() throws Exception {
        DockerClient client = getDockerClient();
        // Remove image to free the used space
        logger.info("Deleting image from local Storage");
        client.removeImage(getTag());
    }

    /**
     Returns a instance of the DockerClient. This represents a factory method and should be overriden if the Building of a custom
     client is needed (for example with Registry credentials)
     */
    protected DockerClient getDockerClient() throws DockerCertificateException {
        // Initialize Docker Client
        logger.debug("Attempting to get connection to the Docker Daemon");
        return DefaultDockerClient.fromEnv().build();
    }

    /**
     This Method gets used to Print the Progress to the Logger.
     Progress and Status messages (Usually representing very verbose datastreams get logged to the Trace level)
     Container output and build output gets logged to the info Level
     Error Outputs get logged to the error level
     */
    @Override
    public void progress(ProgressMessage progressMessage) {
        if (logger.isDebugEnabled()) {
            log(progressMessage.progress(), logger::trace);
            log(progressMessage.status(), logger::trace);
        }
        log(progressMessage.stream(), logger::info);
        log(progressMessage.error(), logger::error);
    }

    /**
     Cleans up a String from unwanted characters such as CR (Carriage Return)
     */
    private String cleanString(String s) {
        //Replace Carriage Return with newline
        return s.replace((char) 0x0D, (char) 0x0A);
    }

    /**
     This method removes the Log string from irrelevant characters and only logs lines containing important content to the given logging function

     @param s           The string to log
     @param loggingFunc the Outputfunction to log to (for example: logger::warn, to log to warn on the logger object)
     */
    private void log(String s, Consumer<String> loggingFunc) {
        if (s != null) {
            String[] lines = s.split("\n");
            for (String line : lines) {
                String[] cleanLine = cleanString(line)
                    //Remove ansi Shell colors
                    .replaceAll("\\x1b\\[[0-9;]*m", "")
                    .split("\n");
                for (String partialLine : cleanLine) {
                    //Dont log a line that only contains pointless chars such as :;.,-_ and spaces
                    if (partialLine.replaceAll("(\\.| |_|-|\\:|;)", "").length() == 0) {
                        continue;
                    }
                    // Log the Output
                    loggingFunc.accept(partialLine);
                }
            }
        }
    }
}
