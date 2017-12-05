package org.opentosca.toscana.model.node;

import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.capability.ContainerCapability.ContainerCapabilityBuilder;
import org.opentosca.toscana.model.requirement.HostRequirement;
import org.opentosca.toscana.model.requirement.Requirement;
import org.opentosca.toscana.model.datatype.Credential;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.relation.HostedOn;
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
                      ContainerCapabilityBuilder containerHostBuilder,
                      String rootPassword,
                      Integer port,
                      String componentVersion,
                      Credential adminCredential,
                      String nodeName,
                      StandardLifecycle lifecycle,
                      String description) {
        super(host, makeValidContainerHost(containerHostBuilder), rootPassword, fallbackPort(port),
            componentVersion, adminCredential, nodeName, lifecycle, description);
    }

    /**
     @param nodeName             {@link #nodeName}
     @param rootPassword         {@link #rootPassword}
     @param host                 {@link #host}
     @param containerHostBuilder {@link #containerHost}
     */
    public static MysqlDbmsBuilder builder(String nodeName,
                                           String rootPassword,
                                           HostRequirement host,
                                           ContainerCapabilityBuilder containerHostBuilder) {
        return (MysqlDbmsBuilder) new MysqlDbmsBuilder()
            .nodeName(nodeName)
            .rootPassword(rootPassword)
            .host(host)
            .containerHostBuilder(containerHostBuilder);
    }

    private static ContainerCapability makeValidContainerHost(ContainerCapabilityBuilder hostBuilder) {
        return hostBuilder.clearValidSourceTypes().validSourceType(MysqlDatabase.class).build();
    }

    private static Integer fallbackPort(Integer port) {
        return (port == null) ? 3306 : port;
    }

    public static class MysqlDbmsBuilder extends DbmsBuilder {
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }
}
