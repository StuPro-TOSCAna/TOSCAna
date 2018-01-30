package org.opentosca.toscana.plugins.kubernetes.docker.dockerfile.builder;

import java.io.File;

import org.opentosca.toscana.UnitTest;
import org.opentosca.toscana.plugins.kubernetes.docker.BaseDockerfileTest;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
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
        String s = builder.compressRunCommands().toString();
        System.out.println(s);
        validate(s, COMPRESSED_PATH);
    }

    @Test
    public void testBuildUncompressed() throws Exception {
        addBaseCommands();
        builder.entrypoint("java", "-jar", "server.jar");
        String s = builder.toString();
        System.out.println(s);
        validate(s, UNCOMPRESSED_PATH);
    }

    @Test
    public void orderedCopyAndEnvTest() {
        String testString1 = "teststringthefirs";
        String testString2 = "teststringthesecond";
        String testString3 = "teststringthethird";
        builder.env(testString1, testString2);
        builder.copyFromWorkingDir(testString1, testString2);
        builder.run(testString1);
        builder.workdir(testString1);
        builder.copyFromWorkingDir(testString2, testString1);
        builder.run(testString2);
        builder.env(testString2, testString1);
        builder.copyFromWorkingDir(testString3, testString1);
        String[] lines = (builder.toString()).split("\n");
        assertEquals(String.format("ENV %s=%s", testString1, testString2), lines[1]);
        assertEquals(String.format("ENV %s=%s", testString2, testString1), lines[2]);
        assertEquals(String.format("RUN %s", testString1), lines[3]);
        assertEquals(String.format("COPY %s %s", testString1, testString2), lines[4]);
        assertEquals(String.format("WORKDIR %s", testString1), lines[5]);
        assertEquals(String.format("COPY %s %s", testString2, testString1), lines[6]);
        assertEquals(String.format("COPY %s %s", testString3, testString1), lines[7]);
        assertEquals(String.format("RUN %s", testString2), lines[8]);
    }
}
