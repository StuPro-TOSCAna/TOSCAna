package org.opentosca.toscana.model.visitor;

import org.opentosca.toscana.model.node.Apache;
import org.opentosca.toscana.model.node.BlockStorage;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.ContainerApplication;
import org.opentosca.toscana.model.node.ContainerRuntime;
import org.opentosca.toscana.model.node.Database;
import org.opentosca.toscana.model.node.Dbms;
import org.opentosca.toscana.model.node.DockerApplication;
import org.opentosca.toscana.model.node.LoadBalancer;
import org.opentosca.toscana.model.node.MysqlDatabase;
import org.opentosca.toscana.model.node.MysqlDbms;
import org.opentosca.toscana.model.node.Nodejs;
import org.opentosca.toscana.model.node.ObjectStorage;
import org.opentosca.toscana.model.node.SoftwareComponent;
import org.opentosca.toscana.model.node.WebApplication;
import org.opentosca.toscana.model.node.WebServer;
import org.opentosca.toscana.model.node.WordPress;
import org.opentosca.toscana.model.node.custom.JavaApplication;
import org.opentosca.toscana.model.node.custom.JavaRuntime;

public interface NodeVisitor {

    default void visit(Apache node) {
        // noop
    }

    default void visit(BlockStorage node) {
        // noop
    }

    default void visit(Compute node) {
        // noop
    }

    default void visit(ContainerApplication node) {
        // noop
    }

    default void visit(ContainerRuntime node) {
        // noop
    }

    default void visit(Database node) {
        // noop
    }

    default void visit(Dbms node) {
        // noop
    }

    default void visit(DockerApplication node) {
        // noop
    }

    default void visit(LoadBalancer node) {
        // noop
    }

    default void visit(MysqlDatabase node) {
        // noop
    }

    default void visit(MysqlDbms node) {
        // noop
    }

    default void visit(Nodejs node) {
        // noop
    }

    default void visit(ObjectStorage node) {
        // noop
    }

    default void visit(SoftwareComponent node) {
        // noop
    }

    default void visit(WebApplication node) {
        // noop
    }

    default void visit(WebServer node) {
        // noop
    }

    default void visit(WordPress node) {
        // noop
    }

    default void visit(JavaApplication node) {
        // noop
    }

    default void visit(JavaRuntime node) {
        // noop
    }
}
