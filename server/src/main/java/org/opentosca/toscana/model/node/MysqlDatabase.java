package org.opentosca.toscana.model.node;

import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.capability.DatabaseEndpointCapability;
import org.opentosca.toscana.model.capability.Requirement;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.relation.HostedOn;
import org.opentosca.toscana.model.visitor.NodeVisitor;
import org.opentosca.toscana.model.visitor.Visitor;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
public class MysqlDatabase extends Database {

    @Getter(AccessLevel.NONE)
    public final Requirement<ContainerCapability, MysqlDbms, HostedOn> host;

    @Builder
    public MysqlDatabase(String databaseName,
                         Integer port,
                         String user,
                         String password,
                         Requirement<ContainerCapability, MysqlDbms, HostedOn> host,
                         DatabaseEndpointCapability databaseEndpoint,
                         String nodeName,
                         StandardLifecycle standardLifecycle,
                         String description) {
        super(databaseName, port, user, password, databaseEndpoint, nodeName, standardLifecycle, description);
        this.host = host;

        requirements.add(host);
    }

    /**
     @param nodeName         {@link #nodeName}
     @param databaseName     {@link #databaseEndpoint}
     @param host             {@link #host}
     @param databaseEndpoint {@link #databaseEndpoint}
     */
    public static MysqlDatabaseBuilder builder(String nodeName,
                                               String databaseName,
                                               DatabaseEndpointCapability databaseEndpoint,
                                               Requirement<ContainerCapability, MysqlDbms, HostedOn> host) {
        return (MysqlDatabaseBuilder) new MysqlDatabaseBuilder()
            .nodeName(nodeName)
            .host(host)
            .databaseName(databaseName)
            .databaseEndpoint(databaseEndpoint);
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }
}
