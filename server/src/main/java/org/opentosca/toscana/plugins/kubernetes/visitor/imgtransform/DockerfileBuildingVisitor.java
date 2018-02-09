package org.opentosca.toscana.plugins.kubernetes.visitor.imgtransform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.model.artifact.Artifact;
import org.opentosca.toscana.model.capability.EndpointCapability;
import org.opentosca.toscana.model.datatype.Port;
import org.opentosca.toscana.model.node.Apache;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.Database;
import org.opentosca.toscana.model.node.Dbms;
import org.opentosca.toscana.model.node.MysqlDatabase;
import org.opentosca.toscana.model.node.MysqlDbms;
import org.opentosca.toscana.model.node.Nodejs;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.node.WebApplication;
import org.opentosca.toscana.model.node.custom.JavaApplication;
import org.opentosca.toscana.model.node.custom.JavaRuntime;
import org.opentosca.toscana.model.operation.Operation;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.relation.ConnectsTo;
import org.opentosca.toscana.model.requirement.Requirement;
import org.opentosca.toscana.model.visitor.NodeVisitor;
import org.opentosca.toscana.plugins.kubernetes.docker.dockerfile.builder.DockerfileBuilder;
import org.opentosca.toscana.plugins.kubernetes.util.NodeStack;
import org.opentosca.toscana.plugins.util.TransformationFailureException;

import org.jgrapht.Graph;
import org.slf4j.Logger;

public class DockerfileBuildingVisitor implements NodeVisitor {

    private final Logger logger;
    private final DockerfileBuilder builder;
    private final NodeStack stack;
    private final Graph<NodeStack, Requirement> connectionGraph;

    private boolean sudoInstalled = false;

    private Set<Integer> ports = new HashSet<>();
    //This list is used to build a entrypoint script if there is more than one startup script
    private List<String> startCommands = new ArrayList<>();

    public DockerfileBuildingVisitor(
        String baseImage,
        NodeStack stack,
        Graph<NodeStack, Requirement> connectionGraph,
        TransformationContext context
    ) {
        this.logger = context.getLogger(getClass());
        this.stack = stack;
        this.connectionGraph = connectionGraph;
        logger.debug("Initializing DockerfileBuilder for {}", stack);
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
    public void visit(Nodejs node) {
        handleDefault(node);
    }

    @Override
    public void visit(JavaRuntime node) {
        handleDefault(node);
    }

    @Override
    public void visit(JavaApplication node) {
        handleDefault(node);
        //Copy over jar and add command to entrypoint
        String jarPath = node.getJar().getFilePath();
        try {
            builder.copyFromCsar(jarPath, "", node.getEntityName() + ".jar");
        } catch (IOException e) {
            throw new TransformationFailureException(
                String.format("Copying jar artifact for node '%s' failed!", node.getEntityName()), e
            );
        }
        startCommands.add(
            String.format(
                "java %s -jar %s.jar %s",
                node.getVmOptions().orElse(""),
                node.getEntityName(),
                node.getArguments().orElse("")
            )
        );
    }

    @Override
    public void visit(Dbms node) {
        if (node.getPort().isPresent()) {
            ports.add(node.getPort().get());
        }
        handleDefault(node);
    }

    @Override
    public void visit(Database node) {
        if (node.getPort().isPresent()) {
            ports.add(node.getPort().get());
        }
        handleDefault(node);
    }

    @Override
    public void visit(Apache node) {
        Set<Requirement> stackConnections = connectionGraph.outgoingEdgesOf(this.stack);
        if (requiresMySQL(stackConnections)) {
            builder.run("docker-php-ext-install mysqli");
        }
        //TODO Add Dependency Detection for apache
        //handleDefault(node);
    }

    @Override
    public void visit(WebApplication node) {
        //If a webapplication does not define a port. add the defaults for HTTP and HTTPS
        if (!node.getAppEndpoint().getPort().isPresent()) {
            ports.add(80);
            ports.add(443);
            //set the ports in the model
            node.getAppEndpoint().setPort(new Port(80));
        }
        handleDefault(node);
    }

    @Override
    public void visit(MysqlDbms node) {
        if (node.getPort().isPresent()) {
            ports.add(node.getPort().get());
        } else {
            //Add mysql default port if none is set
            node.setPort(3306);
            ports.add(3306);
        }
        builder.env("MYSQL_ROOT_PASSWORD", node.getRootPassword().get());
        handleDefault(node);
    }

    @Override
    public void visit(MysqlDatabase node) {
        builder.env("MYSQL_DATABASE", node.getDatabaseName());
        if (node.getUser().isPresent() && !node.getUser().get().equals("root")) {
            builder.env("MYSQL_USER", node.getUser().get());
            builder.env("MYSQL_PASSWORD", node.getPassword().orElse(""));
            if (!node.getPassword().isPresent()) {
                builder.env("MYSQL_ALLOW_EMPTY_PASSWORD", "true");
            }
        }
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
            Map<NodeStack, String> adresses = new HashMap<>();
            node.getCapabilities().forEach(e -> {
                try {
                    if (e instanceof EndpointCapability) {
                        if (((EndpointCapability) e).getPort().isPresent()) {
                            ports.add(((EndpointCapability) e).getPort().get().port);
                        }
                    }
                } catch (Exception ex) {
                    logger.warn("Failed reading Port from node {}", node.getEntityName(), ex);
                }
            });

            //Temporarily set the private address to localhost (127.0.0.1) if there is a connection in the same pod
            //The same compute node
            node.getRequirements().forEach(e -> {
                if (e.getRelationship().isPresent() && e.getRelationship().get() instanceof ConnectsTo) {
                    for (Object o : e.getFulfillers()) {
                        if (o instanceof RootNode) {
                            NodeStack targetStack = this.connectionGraph.vertexSet()
                                .stream().filter(ek -> ek.hasNode((RootNode) o)).findFirst().orElse(null);
                            if (targetStack != null &&
                                targetStack.getComputeNode() == this.stack.getComputeNode()) {
                                adresses.put(this.stack, this.stack.getComputeNode().getPrivateAddress().orElse(null));
                                this.stack.getComputeNode().setPrivateAddress("127.0.0.1");
                            }
                        }
                    }
                }
            });

            addToDockerfile(node.getEntityName(), node.getStandardLifecycle());

            //Reset to original address
            adresses.forEach((k, v) -> {
                k.getComputeNode().setPrivateAddress(v);
            });
        } catch (IOException e) {
            throw new UnsupportedOperationException("Transformation failed while copying artifacts", e);
        }
    }

    private void addToDockerfile(String nodeName, StandardLifecycle lifecycle) throws IOException {
        copyAndExecIfPresent(nodeName, "create", lifecycle.getCreate(), false);
        copyAndExecIfPresent(nodeName, "configure", lifecycle.getConfigure(), false);
        copyAndExecIfPresent(nodeName, "start", lifecycle.getStart(), true);
    }

    private void copyAndExecIfPresent(
        String nodeName,
        String opName,
        Optional<Operation> optionalOperation,
        boolean isStartup
    ) throws IOException {
        if (optionalOperation.isPresent()) {
            optionalOperation.get().getInputs().forEach(e -> {
                if (e.getValue().isPresent()) {
                    logger.info("Adding Environment Variable {}:{}", e.getKey(), e.getValue().get());
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
                    //TODO implement sudo setup for other base images
                    builder.run("apt-get update && apt-get install -y sudo; true");
                    sudoInstalled = true;
                }
                if (path.endsWith(".sql")) return;
                builder.copyFromCsar(path, nodeName, nodeName + "-" + opName);

                String command = "sh " + nodeName + "-" + opName;
                if (!isStartup) {
                    builder.run(command);
                } else {
                    startCommands.add(command);
                }
            }
        }
    }

    private boolean needsSudo(String path) {
        //TODO implement sudo detection mechanism
        return true;
    }

    public Set<Integer> getPorts() {
        return Collections.unmodifiableSet(ports);
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
        if (startCommands.size() == 1) {
            builder.entrypoint(startCommands.get(0));
        }
        builder.write();
    }

    private boolean requiresMySQL(Set<Requirement> stackConnections) {
        boolean hasMySQLConnection = false;
        if (stackConnections != null && stackConnections.size() > 0) {
            for (Requirement connection : stackConnections) {
                for (Object fulfiller : connection.getFulfillers()) {
                    if (fulfiller instanceof MysqlDbms || fulfiller instanceof MysqlDatabase) {
                        hasMySQLConnection = true;
                    }
                }
            }
        }
        return hasMySQLConnection;
    }
}
