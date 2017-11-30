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
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.node.SoftwareComponent;
import org.opentosca.toscana.model.node.WebApplication;
import org.opentosca.toscana.model.node.WebServer;
import org.opentosca.toscana.model.node.WordPress;

public interface NodeVisitor {

    default void visit(Apache node) {
        throw new UnsupportedTypeException(Apache.class);
    }

    default void visit(BlockStorage node) {
        throw new UnsupportedTypeException(BlockStorage.class);
    }

    default void visit(Compute node) {
        throw new UnsupportedTypeException(Compute.class);
    }

    default void visit(ContainerApplication node) {
        throw new UnsupportedTypeException(ContainerApplication.class);
    }

    default void visit(ContainerRuntime node) {
        throw new UnsupportedTypeException(ContainerRuntime.class);
    }

    default void visit(Database node) {
        throw new UnsupportedTypeException(Database.class);
    }

    default void visit(Dbms node) {
        throw new UnsupportedTypeException(Dbms.class);
    }

    default void visit(DockerApplication node) {
        throw new UnsupportedTypeException(DockerApplication.class);
    }

    default void visit(LoadBalancer node) {
        throw new UnsupportedTypeException(LoadBalancer.class);
    }

    default void visit(MysqlDatabase node) {
        throw new UnsupportedTypeException(MysqlDatabase.class);
    }

    default void visit(MysqlDbms node) {
        throw new UnsupportedTypeException(MysqlDbms.class);
    }

    default void visit(Nodejs node) {
        throw new UnsupportedTypeException(Nodejs.class);
    }

    default void visit(ObjectStorage node) {
        throw new UnsupportedTypeException(ObjectStorage.class);
    }

    default void visit(RootNode node) {
        throw new UnsupportedTypeException(RootNode.class);
    }

    default void visit(SoftwareComponent node) {
        throw new UnsupportedTypeException(SoftwareComponent.class);
    }

    default void visit(WebApplication node) {
        throw new UnsupportedTypeException(WebApplication.class);
    }

    default void visit(WebServer node) {
        throw new UnsupportedTypeException(WebServer.class);
    }

    default void visit(WordPress node) {
        throw new UnsupportedTypeException(WordPress.class);
    }
}
