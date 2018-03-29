package org.opentosca.toscana.plugins.kubernetes.visitor.imgtransform;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.opentosca.toscana.plugins.kubernetes.util.SudoUtils;
import org.opentosca.toscana.plugins.util.TransformationFailureException;

import org.jgrapht.Graph;
import org.slf4j.Logger;

/**
 Builds a Dockerfile from a given NodeStack
 */
public class DockerfileBuildingVisitor implements NodeVisitor {

    private static final String COMPOSED_ENTRYPOINT_COMMAND = "sh composed_entrypoint";
    private static final String COMPOSED_ENTRYPOINT_FILENAME = "composed_entrypoint";
    private static final String DOCKER_ROOTPATH = "output/docker/";
    private static final String BIN_SH_SHEBANG = "#!/bin/sh";
    private static final String IPV4_LOCAL_ADDRESS = "127.0.0.1";
    private static final String TOSCANA_ROOT_WORKDIR_PATH = "/toscana-root";
    private static final String APACHE_PHP_MYSQLI_INSTALL_COMMAND = "docker-php-ext-install mysqli";
    private static final String ENV_KEY_MYSQL_ROOT_PASSWORD = "MYSQL_ROOT_PASSWORD";
    private static final String ENV_KEY_MYSQL_DATABASE = "MYSQL_DATABASE";
    private static final String ENV_KEY_MYSQL_USER = "MYSQL_USER";
    private static final String ENV_KEY_MYSQL_PASSWORD = "MYSQL_PASSWORD";
    private static final String ENV_KEY_MYSQL_ALLOW_EMPTY_PASSWORD = "MYSQL_ALLOW_EMPTY_PASSWORD";
    private static final String SUDO_DETECTION_STRING = "sudo ";

    private final TransformationContext context;
    private final Logger logger;
    private final DockerfileBuilder builder;

    private final Set<Integer> ports = new HashSet<>();
    private final List<String> startCommands = new ArrayList<>();
    private final NodeStack stack;
    private final Graph<NodeStack, Requirement> connectionGraph;
    private final String baseImage;

    private boolean sudoInstalled = false;

    public DockerfileBuildingVisitor(
        String baseImage,
        NodeStack stack,
        Graph<NodeStack, Requirement> connectionGraph,
        TransformationContext context
    ) {
        this.context = context;
        this.logger = context.getLogger(getClass());
        this.stack = stack;
        this.connectionGraph = connectionGraph;
        logger.debug("Initializing DockerfileBuilder for {}", stack);
        this.builder = new DockerfileBuilder(
            baseImage,
            DOCKER_ROOTPATH + stack.getStackName(),
            context.getPluginFileAccess()
        );
        builder.workdir(TOSCANA_ROOT_WORKDIR_PATH);
        this.baseImage = baseImage;
    }

    @Override
    public void visit(Compute node) {
        handleDefault(node, new String[] {});
    }

    @Override
    public void visit(Nodejs node) {
        handleDefault(node, new String[] {});
    }

    @Override
    public void visit(JavaRuntime node) {
        handleDefault(node, new String[] {});
    }

    @Override
    public void visit(JavaApplication node) {
        handleDefault(node, new String[] {});
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
        handleDefault(node, new String[] {});
    }

    @Override
    public void visit(Database node) {
        if (node.getPort().isPresent()) {
            ports.add(node.getPort().get());
        }
        handleDefault(node, new String[] {});
    }

    @Override
    public void visit(Apache node) {
        Set<Requirement> stackConnections = connectionGraph.outgoingEdgesOf(this.stack);
        if (requiresMySQL(stackConnections)) {
            builder.run(APACHE_PHP_MYSQLI_INSTALL_COMMAND);
        }
        handleDefault(node, new String[] {"create", "configure"});
    }

    @Override
    public void visit(WebApplication node) {
        // If a Web Application does not define a port. add the defaults for HTTP and HTTPS
        if (!node.getAppEndpoint().getPort().isPresent()) {
            ports.add(80);
            ports.add(443);
            // Set the ports in the model
            node.getAppEndpoint().setPort(new Port(80));
        }
        handleDefault(node, new String[] {});
    }

    @Override
    public void visit(MysqlDbms node) {
        if (node.getPort().isPresent()) {
            ports.add(node.getPort().get());
        } else {
            // Add mysql default port if none is set
            node.setPort(3306);
            ports.add(3306);
        }
        builder.env(ENV_KEY_MYSQL_ROOT_PASSWORD, node.getRootPassword().get());
        handleDefault(node, new String[] {"create", "configure"});
    }

    @Override
    public void visit(MysqlDatabase node) {
        builder.env(ENV_KEY_MYSQL_DATABASE, node.getDatabaseName());
        if (node.getUser().isPresent() && !node.getUser().get().equals("root")) {
            builder.env(ENV_KEY_MYSQL_USER, node.getUser().get());
            builder.env(ENV_KEY_MYSQL_PASSWORD, node.getPassword().orElse(""));
            if (!node.getPassword().isPresent()) {
                builder.env(ENV_KEY_MYSQL_ALLOW_EMPTY_PASSWORD, "true");
            }
        }
        handleDefault(node, new String[] {});
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

            builder.workdir(TOSCANA_ROOT_WORKDIR_PATH);
        }
    }

    /**
     This method implements the default node transformation behaviour. This means, the
     exectuion of scripts implements the functionality thats expected from the node.
     */
    private void handleDefault(RootNode node, String[] ignoredLifecycles) {
        try {
            Map<NodeStack, String> address = new HashMap<>();
            // Add the ports exposed by the node to the ports list 
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

            // Temporarily set the private address to localhost (127.0.0.1) if there is a connection in the same pod
            // i.e. the same compute node
            // This step is necessary, because KubeDNS does not support the the resolution of the self referenced service
            // name. We therefore have to set this to 127.0.0.1 ('localhost' causes issues too)
            for (Requirement e : node.getRequirements()) {
                if (e.getRelationship().isPresent() && e.getRelationship().get() instanceof ConnectsTo) {
                    for (Object o : e.getFulfillers()) {
                        if (o instanceof RootNode) {
                            NodeStack targetStack = this.connectionGraph.vertexSet()
                                .stream().filter(ek -> ek.hasNode((RootNode) o)).findFirst().orElse(null);
                            if (targetStack != null &&
                                targetStack.getComputeNode() == this.stack.getComputeNode()) {
                                address.put(this.stack, this.stack.getComputeNode().getPrivateAddress().orElse(null));
                                this.stack.getComputeNode().setPrivateAddress(IPV4_LOCAL_ADDRESS);
                            }
                        }
                    }
                }
            }

            // Add the scripts from the lifecycle to the Dockerfile
            addLifecycleOperationsToDockerfile(node.getEntityName(), node.getStandardLifecycle(), ignoredLifecycles);

            // Reset to original address
            address.forEach((k, v) -> {
                k.getComputeNode().setPrivateAddress(v);
            });
        } catch (IOException e) {
            throw new UnsupportedOperationException("Transformation failed while copying artifacts", e);
        }
    }

    /**
     calls <code>copyArtifactsOfLifecycleOperation</code> for the create, configure and start lifecycle operations
     */
    private void addLifecycleOperationsToDockerfile(
        String nodeName,
        StandardLifecycle lifecycle,
        String[] ignoredLifecycles
    ) throws IOException {
        copyArtifactsOfLifecycleOperation(nodeName, "create", lifecycle.getCreate(), ignoredLifecycles, false);
        copyArtifactsOfLifecycleOperation(nodeName, "configure", lifecycle.getConfigure(), ignoredLifecycles, false);
        copyArtifactsOfLifecycleOperation(nodeName, "start", lifecycle.getStart(), ignoredLifecycles, true);
    }

    /**
     This method copies the the artifact of the lifecycle operation into the Dockerfile working directory (including its dependencies)
     <p>
     It will also write the Run and Copy commands into the Dockerfile
     <p>
     Properties of the Operations will get mapped as Environment variables (ENV command)
     */
    private void copyArtifactsOfLifecycleOperation(
        String nodeName,
        String opName,
        Optional<Operation> optionalOperation,
        String[] ignoredLifecycles,
        boolean isStartup
    ) throws IOException {
        // Skip ignored lifecycles
        if (Arrays.asList(ignoredLifecycles).contains(opName)) {
            return;
        }

        // Add the artifacts if they are present
        if (optionalOperation.isPresent()) {
            // Set the properties as environment variables
            optionalOperation.get().getInputs().forEach(e -> {
                if (e.getValue().isPresent()) {
                    logger.info("Adding Environment Variable {}:{}", e.getKey(), e.getValue().get());
                    builder.env(e.getKey(), e.getValue().get());
                }
            });
            logger.debug("{} - {} is present", nodeName, opName);
            Operation operation = optionalOperation.get();

            // Copy all Dependencies
            for (String e : operation.getDependencies()) {
                String filename = determineFilename(e);
                builder.copyFromCsar(e, nodeName, filename);
            }

            if (operation.getArtifact().isPresent()) {
                String path = operation.getArtifact().get().getFilePath();

                // Ignore SQL files since their handled elsewhere (for MySQL)
                if (path.endsWith(".sql")) {
                    return;
                }

                // Add the sudo install command if we need sudo
                // this is the case if a script contains "sudo "
                if (needsSudo(path) && !sudoInstalled) {
                    // Install sudo, currently only works with Debian based systems
                    Optional<String> sudocmd = SudoUtils.getSudoInstallCommand(this.baseImage);
                    if (sudocmd.isPresent()) {
                        builder.run(sudocmd.get());
                        sudoInstalled = true;
                    } else {
                        throw new TransformationFailureException(
                            "Cannot determine Sudo install command for base image '" + this.baseImage + "'"
                        );
                    }
                }

                builder.copyFromCsar(path, nodeName, nodeName + "-" + opName);

                String command = "sh " + nodeName + "-" + opName;
                // if the command should be executed on container startup
                // it is considered a entrypoint, and will therefore be added
                // to the startCommands list, later this will result in a entrypoint command
                // Otherwise the command is added as a RUN command and will be executed during
                // the building procedure
                if (!isStartup) {
                    builder.run(command);
                } else {
                    startCommands.add(command);
                }
            }
        }
    }

    /**
     This method looks for the string "sudo " in the given filepath. To find out if the given script
     needs the sudo command (has to be installed)
     */
    private boolean needsSudo(String path) {
        logger.debug("Checking if the Script '{}' needs sudo", path);
        boolean val = false;
        try {
            String s = context.getPluginFileAccess().read(path).toLowerCase();
            val = s.contains(SUDO_DETECTION_STRING);
        } catch (IOException e) {
            logger.warn("Sudo detection for '{}' has failed", path, e);
        }
        logger.debug("The script '{}' does {}need sudo", path, val ? "" : "not ");
        return val;
    }

    /**
     Returns a unmodifiable set of the ports the image exposes
     */
    public Set<Integer> getPorts() {
        return Collections.unmodifiableSet(ports);
    }

    /**
     Determines the filename from a given filepath

     @param path the path to determine the filepath from
     @return the filename
     */
    private String determineFilename(String path) {
        String[] name = path.split("/");
        return name[name.length - 1];
    }

    /**
     Builds the Dockerfile, by iterating over each node and visiting itselt
     */
    public void buildAndWriteDockerfile() throws IOException {
        // Visit all nodes in the node stack
        // It should be ensured that the compute node gets visited first, its direct parent next...
        logger.debug("Visiting nodes");
        stack.forEachNode(node -> {
            logger.debug("Visitng node: {}", node.getNode().getEntityName());
            node.getNode().accept(this);
        });
        // Add expose commands for each port found during the visiting process
        ports.forEach(builder::expose);
        // Set the Entrypoint
        if (startCommands.size() == 1) {
            // Set the only starting Command if there is only one
            builder.entrypoint(startCommands.get(0));
        } else if (startCommands.size() > 1) {
            // Generate the shell script output path
            String outpath = DOCKER_ROOTPATH + this.stack.getStackName() + "/" + COMPOSED_ENTRYPOINT_FILENAME;
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            PrintStream out = new PrintStream(byteOut);

            // Append Shebang (sh to ensure compatibility)
            out.println(BIN_SH_SHEBANG);
            // Append each shell script
            for (int i = 0; i < startCommands.size(); i++) {
                out.print(startCommands.get(i));
                // Fork if the command isnt the last one in the list
                // Forking ensures "parallel" execution of the commands
                if (i < startCommands.size() - 1) {
                    out.println(" &");
                } else {
                    out.println();
                }
            }
            out.close();
            this.context.getPluginFileAccess()
                .access(outpath)
                .append(new String(byteOut.toByteArray()))
                .close();

            // Set the Entrypoint
            builder.copyFromWorkingDir(outpath, COMPOSED_ENTRYPOINT_FILENAME);
            builder.entrypoint(COMPOSED_ENTRYPOINT_COMMAND);
        }

        builder.write();
    }

    /**
     Determines if a node needs MySQL based on the requirements
     This is used for the apache node in order to find out if we have to install mysqli (MySQL driver for PHP)
     */
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
