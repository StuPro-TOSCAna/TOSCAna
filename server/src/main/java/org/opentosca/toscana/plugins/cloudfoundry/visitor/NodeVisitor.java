package org.opentosca.toscana.plugins.cloudfoundry.visitor;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.opentosca.toscana.model.artifact.Artifact;
import org.opentosca.toscana.model.node.Apache;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.MysqlDatabase;
import org.opentosca.toscana.model.node.MysqlDbms;
import org.opentosca.toscana.model.node.Nodejs;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.node.WebApplication;
import org.opentosca.toscana.model.node.custom.JavaApplication;
import org.opentosca.toscana.model.node.custom.JavaRuntime;
import org.opentosca.toscana.model.operation.Operation;
import org.opentosca.toscana.model.operation.OperationVariable;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.relation.ConnectsTo;
import org.opentosca.toscana.model.relation.RootRelationship;
import org.opentosca.toscana.model.visitor.StrictNodeVisitor;
import org.opentosca.toscana.plugins.cloudfoundry.application.Application;
import org.opentosca.toscana.plugins.cloudfoundry.application.ServiceTypes;
import org.opentosca.toscana.plugins.util.TransformationFailureException;

import org.apache.commons.collections.CollectionUtils;
import org.jgrapht.Graph;
import org.slf4j.Logger;

import static org.opentosca.toscana.plugins.cloudfoundry.application.ManifestAttributes.DISKSIZE;
import static org.opentosca.toscana.plugins.cloudfoundry.application.ManifestAttributes.DOMAIN;
import static org.opentosca.toscana.plugins.cloudfoundry.application.ManifestAttributes.MEMORY;

public class NodeVisitor implements StrictNodeVisitor {

    private Application myApp;
    private Map<RootNode, Application> nodeApplicationMap;
    private Graph<RootNode, RootRelationship> topology;
    private final Logger logger;

    public NodeVisitor(Application myApp, Map<RootNode, Application> nodeApplicationMap, Graph<RootNode, RootRelationship> topology, Logger logger) {
        this.myApp = myApp;
        this.nodeApplicationMap = nodeApplicationMap;
        this.logger = logger;
        this.topology = topology;
    }

    public Application getFilledApp() {
        return myApp;
    }

    @Override
    public void visit(Compute node) {
        /*
        os: do nothing -> CF uses linux based containers
        private_address: do nothing -> automatically
        public_address: add value to manifest
        networks: do nothing -> automatically
        ports: do nothing -> automatically
        */

        if (node.getPublicAddress().isPresent()) {
            myApp.addAttribute(DOMAIN.getName(), node.getPublicAddress().get());
        }

        if (node.getHost().getDiskSizeInMb().isPresent()) {
            myApp.addAttribute(DISKSIZE.getName(), node.getHost().getDiskSizeInMb().get() + "MB");
        }

        if (node.getHost().getMemSizeInMb().isPresent()) {
            myApp.addAttribute(MEMORY.getName(), node.getHost().getMemSizeInMb().get() + "MB");
        }
    }

    /**
     a MysqlDatabase is a service in CloudFoundry
     Therefore the service will be added to the application where the source of the connects to relationship is
     */
    @Override
    public void visit(MysqlDatabase node) {
        /*
        create service
        ignore password and port
         */
        logger.debug("Visit Mysql Database");

        Set<RootNode> sourceNodes = getSourcesOfConnectsTo(node);
        Set<Application> belongingApplication = getSourceApplications(sourceNodes);
        if (CollectionUtils.isEmpty(belongingApplication)) {
            logger.error("No source node of connects to relationship of MysqlDatabase {} was found", node.getEntityName());
            throw new TransformationFailureException("Could not find source of database");
        }

        handleStandardLifecycle(node, false, myApp);
        logger.debug("Add MYSQL service to application");
        belongingApplication.forEach(app -> app.addService(node.getEntityName(), ServiceTypes.MYSQL));
        //current application is a dummy application
        myApp.applicationIsNotReal(belongingApplication);

        //check artifacts and add paths to application
        for (Artifact artifact : node.getArtifacts()) {
            String path = artifact.getFilePath();
            myApp.addFilePath(path);
            logger.debug("Add artifact path {} to application", path);
            if (path.endsWith("sql")) {
                myApp.addConfigMysql(path);
                logger.info("Found a SQL script in artifact paths. Will execute it with python script in deployment phase");
            }
        }
    }

    /**
     find all source nodes of the connects to relationship

     @param node current visited node
     @return a set of all source nodes
     */
    private Set<RootNode> getSourcesOfConnectsTo(RootNode node) {
        logger.debug("Try to find source nodes of connects to relationship of '{}'", node.getEntityName());
        Set<RootNode> sourceNodes = new HashSet<>();

        //find all incoming connectsTo edges of the node and save the source Node
        topology.incomingEdgesOf(node).stream()
            .filter(rel -> rel instanceof ConnectsTo)
            .collect(Collectors.toList())
            .forEach(connect -> sourceNodes.add(topology.getEdgeSource(connect)));

        return sourceNodes;
    }

    /**
     find applications to a set of nodes

     @param sourceNodes a set of nodes
     @return a set of application where the nodes belong to
     */
    private Set<Application> getSourceApplications(Set<RootNode> sourceNodes) {
        logger.debug("Try to find belonging applications");
        Set<Application> sourceApplications = new HashSet<>();
        sourceNodes.forEach(node -> sourceApplications.add(nodeApplicationMap.get(node)));
        return sourceApplications;
    }

    @Override
    public void visit(MysqlDbms node) {
        handleStandardLifecycle(node, false, myApp);
    }

    @Override
    public void visit(Apache node) {
        // do nothing yet but override to prevent a UnsupportedTypeException
    }

    @Override
    public void visit(WebApplication node) {
        logger.debug("Visit Web Application");
        myApp.setName(node.getEntityName());
        getScripts(node);
        handleStandardLifecycle(node, true, myApp);
    }

    @Override
    public void visit(Nodejs node) {
        //TODO: Implementation WIP
        logger.debug("Visit Nodejs");
    }

    @Override
    public void visit(JavaRuntime node) {
        //TODO: Implementation WIP
        logger.debug("Visit Java Runtime");
    }

    @Override
    public void visit(JavaApplication node) {
        //TODO: Implementation WIP
        logger.debug("Visit Java Application");
        myApp.setName(node.getEntityName());
        getScripts(node);
        handleStandardLifecycle(node, true, myApp);
    }

    private void handleStandardLifecycle(RootNode node, boolean isTopNode, Application application) {
        // get StandardLifecycle inputs
        for (OperationVariable lifecycleInput : node.getStandardLifecycle().getInputs()) {
            addEnvironmentVariable(lifecycleInput);
        }

        // get operation inputs
        for (Operation operation : node.getStandardLifecycle().getOperations()) {
            // artifact path
            if (operation.getArtifact().isPresent()) {
                String path = operation.getArtifact().get().getFilePath();
                setPathToApplication(path, isTopNode);
            }

            // add dependencies paths
            for (String dependency : operation.getDependencies()) {
                application.addFilePath(dependency);
                setPathToApplication(dependency, isTopNode);
            }

            // add inputs to environment list
            for (OperationVariable input : operation.getInputs()) {
                addEnvironmentVariable(input);
            }
            // TODO: investigate what to do with outputs?
        }
    }

    private void addEnvironmentVariable(OperationVariable input) {
        if (input.getValue().isPresent()) {
            myApp.addEnvironmentVariables(input.getKey(), input.getValue().get());
        } else {
            myApp.addEnvironmentVariables(input.getKey());
        }
    }

    private void setPathToApplication(String path, boolean isTopNode) {
        myApp.addFilePath(path);
        if (myApp.getPathToApplication() == null && isTopNode) {
            myApp.setPathToApplication(path);
        }
    }

    private void getScripts(RootNode node) {
        StandardLifecycle lifecycle = node.getStandardLifecycle();
        Optional<Operation> configureOptional = lifecycle.getConfigure();

        //get configure script
        if (configureOptional.isPresent()) {
            Optional<Artifact> configureArtifact = configureOptional.get().getArtifact();
            configureArtifact.ifPresent(artifact -> myApp.addExecuteFile(artifact.getFilePath(), node));
        }

        //get create script
        Optional<Operation> createOptional = lifecycle.getCreate();
        if (createOptional.isPresent()) {
            Optional<Artifact> createArtifact = createOptional.get().getArtifact();
            createArtifact.ifPresent(artifact -> myApp.addExecuteFile(artifact.getFilePath(), node));
        }
    }
}
