package org.opentosca.toscana.plugins.kubernetes.docker.image;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.transformation.TransformationContext;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerCertificateException;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerException;
import com.spotify.docker.client.ProgressHandler;
import com.spotify.docker.client.messages.ProgressMessage;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

/**
 This class allows the automatic building of a dockerfile if a docker daemon is available.
 */
public class DockerImageBuilder implements ProgressHandler {

    private final Logger logger;
    private final String tag;
    private final String dockerWorkDir;
    private final PluginFileAccess access;

    /**
     @param tag           the tag ("name") that the resulting image should have
     @param dockerWorkDir The working directory (relative to transformation root) that contains the dockerfile
     @param ctx           The TransformationContext used to retrieve the logger and the PluginFileAccess
     */
    public DockerImageBuilder(String tag, String dockerWorkDir, TransformationContext ctx) {
        this.tag = tag;
        this.dockerWorkDir = dockerWorkDir;
        this.logger = ctx.getLogger(getClass());
        this.access = ctx.getPluginFileAccess();
    }

    /**
     @param outputPath The relative path to the file (with name and ending (should be .tar.gz))
     @throws IOException                Gets thrown if a general IO Error occurs
     @throws DockerCertificateException gets thrown if there is a certificate issue when connecting to the Docker
     daemon
     @throws DockerException            Gets thrown if something goes wrong while executing a docker command
     @throws InterruptedException       Gets thrown if a awaiting process gets interrupted
     */
    public void buildImage(String outputPath)
        throws IOException, DockerCertificateException, DockerException, InterruptedException {
        // Initialize Docker Client
        logger.debug("Attempting to get connection to the Docker Daemon");
        Path abs = Paths.get(access.getAbsolutePath(dockerWorkDir));
        DockerClient client = DefaultDockerClient.fromEnv().build();
        // Build the image
        String result = client.build(abs, tag, this);
        logger.info("Image build was successful. Image ID: {}", result);

        // Export the image
        logger.info("Saving image to {}", outputPath);
        OutputStream out = access.accessAsOutputStream(outputPath);

        InputStream in = client.save(tag);

        IOUtils.copy(in, out);
        in.close();
        out.close();

        // Remove image to free the used space
        logger.info("Deleting image from local Storage");
        client.removeImage(tag);
    }

    @Override
    public void progress(ProgressMessage progressMessage) throws DockerException {
        String stream = progressMessage.stream();
        String error = progressMessage.error();
        if (stream != null) {
            logger.info(stream.replace("\n", " "));
        }
        if (error != null) {
            logger.error(error.replace("\n", " "));
        }
    }
}
