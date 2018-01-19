package org.opentosca.toscana.plugins.kubernetes.visitor.imgtransform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.model.artifact.Artifact;
import org.opentosca.toscana.model.node.Apache;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.MysqlDatabase;
import org.opentosca.toscana.model.node.MysqlDbms;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.node.WebApplication;
import org.opentosca.toscana.model.operation.Operation;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.visitor.NodeVisitor;
import org.opentosca.toscana.plugins.kubernetes.docker.dockerfile.builder.DockerfileBuilder;
import org.opentosca.toscana.plugins.kubernetes.util.NodeStack;
import org.opentosca.toscana.plugins.util.TransformationFailureException;

import org.slf4j.Logger;

public class DockerfileBuildingVisitor implements NodeVisitor {

    private final Logger logger;
    private final DockerfileBuilder builder;
    private final NodeStack stack;

    private boolean sudoInstalled = false;

    private List<Integer> ports = new ArrayList<>();

    public DockerfileBuildingVisitor(String baseImage, NodeStack stack, TransformationContext context) {
        this.logger = context.getLogger(getClass());
        this.stack = stack;
        logger.debug("Initiialing DockerfileBilder for {}", stack);
        this.builder = new DockerfileBuilder(
            baseImage,
            "output/docker/" + stack.getStackName(),
            context.getPluginFileAccess()
        );
        builder.workdir("/toscana-root");
    }

    @Override
    public void visit(Compute node) {
        handleDefault(node);
    }

    @Override
    public void visit(Apache node) {
        ports.add(80);
        ports.add(443);
        //TODO only do this if a Mysql node connects to (is in a connection relationship with this node)
        builder.run("docker-php-ext-install mysqli");
        //TODO Add Dependency Detection for apache
        //handleDefault(node);
    }

    @Override
    public void visit(WebApplication node) {
        handleDefault(node);
    }

    @Override
    public void visit(MysqlDbms node) {
        ports.add(3306);
        builder.env("MYSQL_ROOT_PASSWORD", node.getRootPassword().get());
        handleDefault(node);
    }

    @Override
    public void visit(MysqlDatabase node) {
        builder.env("MYSQL_DATABASE", node.getDatabaseName());
        handleDefault(node);
        List<Optional<Operation>> lifecycles = new ArrayList<>();
        lifecycles.add(node.getStandardLifecycle().getConfigure());
        lifecycles.add(node.getStandardLifecycle().getCreate());
        if (lifecycles.stream().anyMatch(Optional::isPresent)) {
            builder.workdir("/docker-entrypoint-initdb.d");

            lifecycles.forEach(e -> {
                if (e.isPresent()) {
                    Operation operation = e.get();
                    Optional<Artifact> artifact = operation.getArtifact();
                    if (artifact.isPresent()) {
                        String path = artifact.get().getFilePath();
                        if (path.endsWith(".sql")) {
                            String filename = determineFilename(path);
                            try {
                                builder.copyFromCsar(path, "", filename);
                            } catch (IOException ex) {
                                logger.error("Copying dependencies of node {} has failed!", node.getEntityName(), ex);
                                throw new TransformationFailureException("Copying dependencies failed", ex);
                            }
                        }
                    }
                }
            });

            builder.workdir("/toscana-root");
        }
    }

    private void handleDefault(RootNode node) {
        try {
            addToDockerfile(node.getEntityName(), node.getStandardLifecycle());
        } catch (IOException e) {
            throw new UnsupportedOperationException("Transformation failed while copying artifacts", e);
        }
    }

    private void addToDockerfile(String nodeName, StandardLifecycle lifecycle) throws IOException {
        copyAndExecIfPresent(nodeName, "create", lifecycle.getCreate());
        copyAndExecIfPresent(nodeName, "configure", lifecycle.getConfigure());
    }

    private void copyAndExecIfPresent(String nodeName, String opName, Optional<Operation> optionalOperation)
        throws IOException {
        if (optionalOperation.isPresent()) {
            optionalOperation.get().getInputs().forEach(e -> {
                if (e.getValue().isPresent()) {
                    builder.env(e.getKey(), e.getValue().get());
                }
            });
            logger.debug("{} - {} is present", nodeName, opName);
            Operation operation = optionalOperation.get();
            for (String e : operation.getDependencies()) {
                String filename = determineFilename(e);
                builder.copyFromCsar(e, nodeName, filename);
            }
            if (operation.getArtifact().isPresent()) {
                String path = operation.getArtifact().get().getFilePath();
                if (!sudoInstalled && needsSudo(path)) {
                    //Install sudo, currently only works with Debian based systems
                    builder.run("apt-get update && apt-get install -y sudo; true");
                    sudoInstalled = true;
                }
                if (path.endsWith(".sql")) return;
                builder.copyFromCsar(path, nodeName, nodeName + "-" + opName);
                builder.run("sh " + nodeName + "-" + opName);
            }
        }
    }

    private boolean needsSudo(String path) {
        //TODO implement sudo detection mechanism
        return true;
    }

    public List<Integer> getPorts() {
        return Collections.unmodifiableList(ports);
    }

    private String determineFilename(String path) {
        String[] name = path.split("/");
        return name[name.length - 1];
    }

    public void buildAndWriteDockerfile() throws IOException {
        logger.debug("Visiting nodes");
        stack.forEachNode(node -> {
            logger.debug("Visitng node: {}", node.getNode().getEntityName());
            node.getNode().accept(this);
        });
        builder.write();
    }
}
