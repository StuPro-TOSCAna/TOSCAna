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

/**
 Unimplemented methods throw an {@link UnsupportedTypeException} when invoked.
 */
public interface StrictNodeVisitor extends NodeVisitor {

    @Override
    default void visit(Apache node) {
        throw new UnsupportedTypeException(Apache.class);
    }

    @Override
    default void visit(BlockStorage node) {
        throw new UnsupportedTypeException(BlockStorage.class);
    }

    @Override
    default void visit(Compute node) {
        throw new UnsupportedTypeException(Compute.class);
    }

    @Override
    default void visit(ContainerApplication node) {
        throw new UnsupportedTypeException(ContainerApplication.class);
    }

    @Override
    default void visit(ContainerRuntime node) {
        throw new UnsupportedTypeException(ContainerRuntime.class);
    }

    @Override
    default void visit(Database node) {
        throw new UnsupportedTypeException(Database.class);
    }

    @Override
    default void visit(Dbms node) {
        throw new UnsupportedTypeException(Dbms.class);
    }

    @Override
    default void visit(DockerApplication node) {
        throw new UnsupportedTypeException(DockerApplication.class);
    }

    @Override
    default void visit(LoadBalancer node) {
        throw new UnsupportedTypeException(LoadBalancer.class);
    }

    @Override
    default void visit(MysqlDatabase node) {
        throw new UnsupportedTypeException(MysqlDatabase.class);
    }

    @Override
    default void visit(MysqlDbms node) {
        throw new UnsupportedTypeException(MysqlDbms.class);
    }

    @Override
    default void visit(Nodejs node) {
        throw new UnsupportedTypeException(Nodejs.class);
    }

    @Override
    default void visit(ObjectStorage node) {
        throw new UnsupportedTypeException(ObjectStorage.class);
    }

    @Override
    default void visit(SoftwareComponent node) {
        throw new UnsupportedTypeException(SoftwareComponent.class);
    }

    @Override
    default void visit(WebApplication node) {
        throw new UnsupportedTypeException(WebApplication.class);
    }

    @Override
    default void visit(WebServer node) {
        throw new UnsupportedTypeException(WebServer.class);
    }

    @Override
    default void visit(WordPress node) {
        throw new UnsupportedTypeException(WordPress.class);
    }

    @Override
    default void visit(JavaRuntime node) {
        throw new UnsupportedTypeException(node.getClass());
    }

    @Override
    default void visit(JavaApplication node) {
        throw new UnsupportedTypeException(node.getClass());
    }
}
