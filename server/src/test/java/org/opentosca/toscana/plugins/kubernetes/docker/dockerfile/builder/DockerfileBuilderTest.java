package org.opentosca.toscana.plugins.kubernetes.docker.dockerfile.builder;

import java.io.File;

import org.opentosca.toscana.plugins.kubernetes.docker.BaseDockerfileTest;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class DockerfileBuilderTest extends BaseDockerfileTest {

    private static final String UNCOMPRESSED_PATH = "kubernetes/docker/df_uncompressed";
    private static final String COMPRESSED_PATH = "kubernetes/docker/df_compressed";

    @Test
    public void testBuildCopySingeFileFromInput() throws Exception {
        buildSHADockerfile();

        assertTrue(new File(workDir, "docker-img/bin/test.bin").exists());
        assertTrue(new File(workDir, "docker-img/Dockerfile").exists());
    }

    @Test
    public void testBuildCompressed() throws Exception {
        addBaseCommands();
        builder.entrypoint("java", "-jar", "server.jar");
        String s = builder.compressRunCommands().buildToString();
        System.out.println(s);
        validate(s, COMPRESSED_PATH);
    }

    @Test
    public void testBuildUncompressed() throws Exception {
        addBaseCommands();
        builder.entrypoint("java", "-jar", "server.jar");
        String s = builder.buildToString();
        System.out.println(s);
        validate(s, UNCOMPRESSED_PATH);
    }
}
