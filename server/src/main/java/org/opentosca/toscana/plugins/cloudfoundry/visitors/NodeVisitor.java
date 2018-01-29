package org.opentosca.toscana.plugins.cloudfoundry.visitors;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.model.artifact.Artifact;
import org.opentosca.toscana.model.node.Apache;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.MysqlDatabase;
import org.opentosca.toscana.model.node.MysqlDbms;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.node.WebApplication;
import org.opentosca.toscana.model.operation.Operation;
import org.opentosca.toscana.model.operation.OperationVariable;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.requirement.Requirement;
import org.opentosca.toscana.model.visitor.StrictNodeVisitor;
import org.opentosca.toscana.plugins.cloudfoundry.application.Application;
import org.opentosca.toscana.plugins.cloudfoundry.application.ServiceTypes;
import org.opentosca.toscana.plugins.util.TransformationFailureException;

import org.slf4j.Logger;

import static org.opentosca.toscana.plugins.cloudfoundry.application.ManifestAttributes.DISKSIZE;
import static org.opentosca.toscana.plugins.cloudfoundry.application.ManifestAttributes.DOMAIN;
import static org.opentosca.toscana.plugins.cloudfoundry.application.ManifestAttributes.MEMORY;

public class NodeVisitor implements StrictNodeVisitor {

    private Application myApp;
    private Map<RootNode, Application> nodeApplicationMap;
    private Set<RootNode> allNodes;
    private final Logger logger;

    public NodeVisitor(Application myApp, Map<RootNode, Application> nodeApplicationMap, Set<RootNode> allNodes, Logger logger) {
        this.myApp = myApp;
        this.nodeApplicationMap = nodeApplicationMap;
        this.allNodes = allNodes;
        this.logger = logger;
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

        Application belongingApplication = getSourceOfDatabase(node);
        if (belongingApplication == null) {
            logger.error("No source node of connects to relationship of MysqlDatabase {} was found", node.getEntityName());
            throw new TransformationFailureException("Could not find source of database");
        }

        handleStandardLifecycle(node, false, belongingApplication);
        logger.debug("Add MYSQL service to application");
        belongingApplication.addService(node.getEntityName(), ServiceTypes.MYSQL);
        //current application is a dummy application
        myApp.applicationIsNotReal(belongingApplication);

        //check artifacts and add paths to application
        for (Artifact artifact : node.getArtifacts()) {
            String path = artifact.getFilePath();
            belongingApplication.addFilePath(path);
            logger.debug("Add artifact path {} to application", path);
            if (path.endsWith("sql")) {
                belongingApplication.addConfigMysql(path);
                logger.info("Found a SQL script in artifact paths. Will execute it with python script in deployment phase");
            }
        }
    }

    /**
     find the source of the connects to relationship of a node and return the application where it belongs to

     @param node target of the connects to relation, current visited node
     @return the application where the node belongs to
     */
    private Application getSourceOfDatabase(RootNode node) {
        logger.debug("Try to find source of the requirement");
        for (RootNode tmpNode : allNodes) {
            //check each requirement of each node
            for (Requirement requirement : tmpNode.getRequirements()) {
                //check the fullfillers of this node
                for (Object fullFiller : requirement.getFulfillers()) {
                    RootNode fullFillerNode = (RootNode) fullFiller;
                    //if the fullFillerNode is the current node, then we found the source node of 
                    //the connects to relationship
                    if (fullFillerNode == node) {
                        return nodeApplicationMap.get(tmpNode);
                    }
                }
            }
        }
        return null;
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
        myApp.setName(node.getEntityName());

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
}
