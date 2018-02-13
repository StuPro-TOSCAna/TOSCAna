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

public abstract class ImageBuilder implements ProgressHandler {

    protected final Logger logger;
    protected final String dockerWorkDir;
    protected final PluginFileAccess access;

    private final String tag;

    /**
     @param tag           the tag ("name") that the resulting image should have
     @param dockerWorkDir The working directory (relative to transformation root) that contains the dockerfile
     @param context       The TransformationContext used to retrieve the logger and the PluginFileAccess
     */
    public ImageBuilder(String tag, String dockerWorkDir, TransformationContext context) {
        this.tag = tag;
        this.dockerWorkDir = dockerWorkDir;
        this.access = context.getPluginFileAccess();
        this.logger = context.getLogger(getClass());
    }

    public void buildImage() throws Exception {
        DockerClient client = getDockerClient();

        Path abs = Paths.get(access.getAbsolutePath(dockerWorkDir));
        // Build the image
        String result = client.build(abs, getTag(), this);
        logger.info("Image build was successful. Image ID: {}", result);
    }

    public String getTag() {
        return tag;
    }

    public abstract void storeImage() throws Exception;

    public void cleanup() throws Exception {
        DockerClient client = getDockerClient();
        // Remove image to free the used space
        logger.info("Deleting image from local Storage");
        client.removeImage(getTag());
    }

    protected DockerClient getDockerClient() throws DockerCertificateException {
        // Initialize Docker Client
        logger.debug("Attempting to get connection to the Docker Daemon");
        return DefaultDockerClient.fromEnv().build();
    }

    @Override
    public void progress(ProgressMessage progressMessage) {
        if (logger.isDebugEnabled()) {
            log(progressMessage.progress(), logger::trace);
            log(progressMessage.status(), logger::trace);
        }
        log(progressMessage.stream(), logger::info);
        log(progressMessage.error(), logger::error);
    }

    private String cleanString(String s) {
        //Replace Carriage Return with newline
        return s.replace((char) 0x0D, (char) 0x0A);
    }

    private void log(String s, Consumer<String> loggingFunc) {

        if (s != null) {
            String[] lines = s.split("\n");
            for (String line : lines) {
                String[] cleanLine = cleanString(line)
                    //Remove ansi Shell colors
                    .replaceAll("\\x1b\\[[0-9;]*m", "")
                    .split("\n");
                for (String partialLine : cleanLine) {
                    if (partialLine.replaceAll("(\\.| |_|-|\\:|;)", "").length() == 0) {
                        continue;
                    }
                    loggingFunc.accept(partialLine);
                }
            }
        }
    }
}
