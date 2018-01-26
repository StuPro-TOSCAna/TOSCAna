package org.opentosca.toscana.plugins.cloudfoundry.visitors;

import java.util.Optional;

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
import org.opentosca.toscana.model.visitor.StrictNodeVisitor;
import org.opentosca.toscana.plugins.cloudfoundry.application.Application;
import org.opentosca.toscana.plugins.cloudfoundry.application.ServiceTypes;

import static org.opentosca.toscana.plugins.cloudfoundry.application.ManifestAttributes.DISKSIZE;
import static org.opentosca.toscana.plugins.cloudfoundry.application.ManifestAttributes.DOMAIN;
import static org.opentosca.toscana.plugins.cloudfoundry.application.ManifestAttributes.MEMORY;

public class NodeVisitor implements StrictNodeVisitor {

    private Application myApp;

    public NodeVisitor(Application myApp) {
        this.myApp = myApp;
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

    @Override
    public void visit(MysqlDatabase node) {
        /*
        create service
        ignore password and port
         */
        handleStandardLifecycle(node, false);
        myApp.addService(node.getEntityName(), ServiceTypes.MYSQL);
        for (Artifact artifact : node.getArtifacts()) {
            String path = artifact.getFilePath();
            myApp.addFilePath(path);
            if (path.endsWith("sql")) {
                myApp.addConfigMysql(path);
            }
        }
    }

    @Override
    public void visit(MysqlDbms node) {
        // TODO: check how to configure database
        handleStandardLifecycle(node, false);
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

        handleStandardLifecycle(node, true);
    }

    private void handleStandardLifecycle(RootNode node, boolean isTopNode) {
        // get StandardLifecycle inputs
        for (OperationVariable lifecycleInput : node.getStandardLifecycle().getInputs()) {
            addEnvironmentVariable(lifecycleInput);
        }

        /*
        // read artifact file paths
        for (Artifact artifact : node.getArtifacts()) {
            String path = artifact.getFilePath();
            myApp.addFilePath(path);
        }
        */

        // get operation inputs
        for (Operation operation : node.getStandardLifecycle().getOperations()) {
            // artifact path
            if (operation.getArtifact().isPresent()) {
                String path = operation.getArtifact().get().getFilePath();
                setPathToApplication(path, isTopNode);
            }

            // add dependencies paths
            for (String dependency : operation.getDependencies()) {
                myApp.addFilePath(dependency);
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
