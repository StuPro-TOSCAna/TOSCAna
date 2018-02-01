package org.opentosca.toscana.plugins.kubernetes.docker;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.opentosca.toscana.core.BaseIntegrationTest;
import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.plugins.kubernetes.docker.dockerfile.builder.DockerfileBuilder;
import org.opentosca.toscana.plugins.kubernetes.docker.dockerfile.builder.DockerfileBuilderTest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

public abstract class BaseDockerfileTest extends BaseIntegrationTest {

    protected static final String INPUT_DIR_NAME = "image-input";
    protected static final String WORKING_DIR_NAME = "test-image";
    protected static final String WORKING_DIR_SUBFOLDER_NAME = "docker-img";

    private static final Logger logger = LoggerFactory.getLogger(DockerfileBuilderTest.class);

    protected DockerfileBuilder builder;

    protected PluginFileAccess access;

    protected File inputDir;
    protected File workDir;
    protected File dockerWorkdir;

    @Before
    public void setUp() {
        inputDir = new File(tmpdir, INPUT_DIR_NAME);
        workDir = new File(tmpdir, WORKING_DIR_NAME);
        dockerWorkdir = new File(workDir, WORKING_DIR_SUBFOLDER_NAME);

        logger.info("Initialing Environment");
        inputDir.mkdirs();
        workDir.mkdirs();

        access = new PluginFileAccess(inputDir, workDir, logMock());
        builder = new DockerfileBuilder("library/ubuntu:latest", WORKING_DIR_SUBFOLDER_NAME, access);
    }

    public String buildSHADockerfile() throws Exception {
        File bindir = new File(inputDir, "bin");
        bindir.mkdirs();
        byte[] data = new byte[1024 * 1024 * 10];
        new Random().nextBytes(data);

        FileUtils.writeByteArrayToFile(new File(bindir, "test.bin"), data);

        builder.run("apt update")
            .run("apt upgrade -y")
            .workdir("/hello")
            .copyFromCsar("bin", "bin", ".")
            .compressRunCommands()
            .entrypoint("sha256sum", "test.bin");

        builder.write();
        return DigestUtils.sha256Hex(data);
    }

    public void validate(String s, String path) throws IOException {
        List<String> lines = IOUtils.readLines(
            getClass().getClassLoader().getResourceAsStream(path),
            "UTF-8"
        );
        List<String> lines2 = Arrays.asList(s.split("\n"));
        for (int i = 0; i < lines.size(); i++) {
            assertEquals(lines.get(i), lines2.get(i));
        }
    }

    protected void addBaseCommands() {
        builder.run("apt-get update")
            .run("apt-get upgrade -y")
            .run("apt-get install -y openjdk8-jdk")
            .run("apt-get install -y maven")
            .run("apt-get install -y git")
            .workdir("/toscana")
            .run("git clone https://github.com/StuPro-TOSCAna/TOSCAna.git")
            .run("cd TOSCAna && mvn install -DskipTests")
            .expose(8080)
            .volume("/toscana/data")
            .run("mv TOSCAna/server/target/server-1.0-SNAPSHOT.jar server.jar")
            .run("apt-get autoremove -y maven git")
            .run("rm -r /root/.m2")
            .run("rm -r TOSCAna");
    }
}
