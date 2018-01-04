package org.opentosca.toscana.plugins.kubernetes.docker.image;

import java.io.InputStream;
import java.io.OutputStream;

import org.opentosca.toscana.core.transformation.TransformationContext;

import com.spotify.docker.client.DockerClient;
import org.apache.commons.io.IOUtils;

/**
 This class allows the automatic building of a dockerfile if a docker daemon is available.
 */
public class ExportingImageBuilder extends ImageBuilder {

    private final String outputPath;

    /**
     @param outputPath the path to which the tar file should be written.
     */
    public ExportingImageBuilder(String outputPath, String tag, String dockerWorkDir, TransformationContext ctx) {
        super(tag, dockerWorkDir, ctx);
        this.outputPath = outputPath;
    }

    @Override
    public void storeImage() throws Exception {
        DockerClient client = getDockerClient();
        // Export the image
        logger.info("Saving image to {}", outputPath);
        OutputStream out = access.accessAsOutputStream(outputPath);

        InputStream in = client.save(getTag());

        IOUtils.copy(in, out);
        in.close();
        out.close();
    }
}
