package org.opentosca.toscana.plugins.kubernetes;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.opentosca.toscana.plugins.model.DockerApp;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.text.StrBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DockerImageBuildScript {
    private final static Logger logger = LoggerFactory.getLogger(DockerImageBuildScript.class);
    private final static String BUILD_SCRIPT_BLOCK_PATH = "/kubernetes/BuildScript/";
    private List<DockerApp> dockerApps;
    private StrBuilder result;

    public DockerImageBuildScript() {
        this.dockerApps = new ArrayList<>();
        this.result = new StrBuilder();
    }

    public void addDockerApp(DockerApp app) {
        dockerApps.add(app);
    }

    public String generateBuildScript() throws IOException, DockerImageBuildScriptException {
        logger.info("Started to generate build script.");
        appendShebang();
        appendDockerExecutableCheck();

        for (DockerApp app : dockerApps) {
            logger.info("Adding build line for " + app.getTag()[1] + ".");
            List<String> dependencies = app.getDependencies();

            String appRoot = findRootFolder(dependencies);
            if (appRoot == null) {
                throw new DockerImageBuildScriptException("DockerApp has no Dockerfile!");
            }
            appendDockerBuildLine(appRoot, app.getTag()[1]);
            appendSuccessfulBuildCheck();
        }
        logger.info("Generating build script was successful!");
        return result.toString();
    }

    private String findRootFolder(List<String> dependencies) {
        for (String s : dependencies) {
            if (s.contains("Dockerfile")) {
                return s.replace("Dockerfile", "");
            }
        }
        return null;
    }

    private void appendShebang() {
        result.appendln("#!/bin/sh");
        result.appendNewLine();
    }

    private void appendDockerExecutableCheck() throws IOException {
        appendBuildScriptBlock(BUILD_SCRIPT_BLOCK_PATH + "docker_executable_check.sh");
        result.appendNewLine();
    }

    private void appendDockerBuildLine(String dockerAppRootPath, String tag) {
        result.appendln("docker image -t " + tag + " ../" + dockerAppRootPath);
        result.appendNewLine();
    }

    private void appendSuccessfulBuildCheck() throws IOException {
        appendBuildScriptBlock(BUILD_SCRIPT_BLOCK_PATH + "docker_image_build_check.sh");
    }

    private void appendBuildScriptBlock(String path) throws IOException {
        InputStream stream = this.getClass().getResourceAsStream(path);
        result.append(IOUtils.toString(stream));
    }

    public class DockerImageBuildScriptException extends Exception {
        public DockerImageBuildScriptException(String s) {
            super(s);
        }
    }
}
