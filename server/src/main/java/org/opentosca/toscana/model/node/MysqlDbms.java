package org.opentosca.toscana.model.node;

import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.datatype.Credential;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.requirement.HostRequirement;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.Builder;
import lombok.Data;

/**
 A MysqlDbms database (server)
 TOSCA Simple Profile in YAML Version 1.1, p. 221)
 */
@Data
public class MysqlDbms extends Dbms {

    @Builder
    private MysqlDbms(HostRequirement host,
                      ContainerCapability containerHost,
                      String rootPassword,
                      Integer port,
                      String componentVersion,
                      Credential adminCredential,
                      String nodeName,
                      StandardLifecycle lifecycle,
                      String description) {
        super(HostRequirement.getFallback(host), makeValidContainerHost(containerHost), rootPassword, fallbackPort(port),
            componentVersion, adminCredential, nodeName, lifecycle, description);
    }

    /**
     @param nodeName     {@link #nodeName}
     @param rootPassword {@link #rootPassword}
     */
    public static MysqlDbmsBuilder builder(String nodeName,
                                           String rootPassword) {
        return new MysqlDbmsBuilder()
            .nodeName(nodeName)
            .rootPassword(rootPassword);
    }

    private static ContainerCapability makeValidContainerHost(ContainerCapability host) {
        host = ContainerCapability.getFallback(host);
        host.getValidSourceTypes().add((MysqlDatabase.class));
        return host;
    }

    private static Integer fallbackPort(Integer port) {
        return (port == null) ? 3306 : port;
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }

    public static class MysqlDbmsBuilder extends DbmsBuilder {
    }
}
