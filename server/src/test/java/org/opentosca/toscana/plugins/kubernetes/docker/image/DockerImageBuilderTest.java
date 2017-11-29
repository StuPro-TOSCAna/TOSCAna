package org.opentosca.toscana.plugins.kubernetes.docker.image;

import java.io.File;

import org.opentosca.toscana.plugins.kubernetes.docker.BaseDockerfileTest;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assume.assumeTrue;

public class DockerImageBuilderTest extends BaseDockerfileTest {

    private static final Logger logger = LoggerFactory.getLogger(DockerImageBuilderTest.class);
    private static final String SHA_TEST_FILE_NAME = "sha-test.tar.gz";

    private DockerClient client;

    @Before
    public void init() throws Exception {
        client = DefaultDockerClient.fromEnv().build();
    }

    @Test
    public void testBuildShaImage() throws Exception {
        assumeTrue(isDockerAvailable());
        //Create Dockerfile and the corresponding Binary file
        buildSHADockerfile();

        DockerImageBuilder imageBuilder = new DockerImageBuilder(
            "toscana/sha256-test:test",
            WORKING_DIR_SUBFOLDER_NAME,
            access
        );
        imageBuilder.buildImage(SHA_TEST_FILE_NAME);

        File d = new File(access.getAbsolutePath(SHA_TEST_FILE_NAME));

        assertTrue(d.length() > 120 * 1024 * 1024);
    }

    private boolean isDockerAvailable() {
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
