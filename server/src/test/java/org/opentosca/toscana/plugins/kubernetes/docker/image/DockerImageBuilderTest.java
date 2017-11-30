package org.opentosca.toscana.plugins.kubernetes.docker.image;

import java.io.File;

import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.plugins.kubernetes.docker.BaseDockerfileTest;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assume.assumeTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DockerImageBuilderTest extends BaseDockerfileTest {

    private static final Logger logger = LoggerFactory.getLogger(DockerImageBuilderTest.class);
    private static final String SHA_TEST_FILE_NAME = "sha-test.tar.gz";
    
    @Test
    public void testBuildShaImage() throws Exception {
        assumeTrue(isDockerAvailable());
        //Create Dockerfile and the corresponding Binary file
        buildSHADockerfile();

        TransformationContext ctx = mock(TransformationContext.class);
        when(ctx.getPluginFileAccess()).thenReturn(access);
        when(ctx.getLogger((Class<?>) any(Class.class))).thenReturn(LoggerFactory.getLogger("Mock Logger"));
        
        DockerImageBuilder imageBuilder = new DockerImageBuilder(
            "toscana/sha256-test:test",
            WORKING_DIR_SUBFOLDER_NAME,
            ctx
        );
        imageBuilder.buildImage(SHA_TEST_FILE_NAME);

        File d = new File(access.getAbsolutePath(SHA_TEST_FILE_NAME));

        assertTrue(d.length() > 120 * 1024 * 1024);
    }

    private boolean isDockerAvailable() {
        //This is a Quick solution to prevent this test from beeing executed if the environment variable is not set.
        //TODO Remove if there is Proper differing between integration and unit tests.
        String env = System.getenv("TEST_DOCKER");
        if(env == null || !env.equalsIgnoreCase("true")) {
            return false;
        }
        try {
            if (DefaultDockerClient.fromEnv().build() == null) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
