package org.opentosca.toscana.model.node;

import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.capability.ContainerCapability.ContainerCapabilityBuilder;
import org.opentosca.toscana.model.datatype.Credential;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.visitor.Visitor;

import lombok.Builder;
import lombok.Data;

/**
 A MysqlDbms database (server)
 TOSCA Simple Profile in YAML Version 1.1, p. 221)
 */
@Data
public class MysqlDbms extends Dbms {

    @Builder
    private MysqlDbms(ContainerCapabilityBuilder hostBuilder,
                      String rootPassword,
                      Integer port,
                      String componentVersion,
                      Credential adminCredential,
                      String nodeName,
                      StandardLifecycle lifecycle,
                      String description) {
        super(makeValidHost(hostBuilder), rootPassword, fallbackPort(port),
            componentVersion, adminCredential, nodeName, lifecycle, description);
    }

    /**
     @param nodeName     {@link #nodeName}
     @param rootPassword {@link #rootPassword}
     @param host         {@link #host}
     */
    public static MysqlDbmsBuilder builder(String nodeName,
                                           String rootPassword,
                                           ContainerCapability host) {
        return (MysqlDbmsBuilder) new MysqlDbmsBuilder()
            .nodeName(nodeName)
            .rootPassword(rootPassword)
            .host(host);
    }

    private static ContainerCapability makeValidHost(ContainerCapabilityBuilder hostBuilder) {
        return hostBuilder.clearValidSourceTypes().validSourceType(MysqlDatabase.class).build();
    }

    private static Integer fallbackPort(Integer port) {
        return (port == null) ? 3306 : port;
    }

    public static class MysqlDbmsBuilder extends DbmsBuilder {
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
