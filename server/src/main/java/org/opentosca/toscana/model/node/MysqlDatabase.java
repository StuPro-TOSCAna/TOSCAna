package org.opentosca.toscana.model.node;

import org.opentosca.toscana.model.capability.DatabaseEndpointCapability;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.requirement.MysqlDbmsRequirement;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
public class MysqlDatabase extends Database {

    @Getter(AccessLevel.NONE)
    public final MysqlDbmsRequirement host;

    @Builder
    public MysqlDatabase(String databaseName,
                         Integer port,
                         String user,
                         String password,
                         MysqlDbmsRequirement host,
                         DatabaseEndpointCapability databaseEndpoint,
                         String nodeName,
                         StandardLifecycle standardLifecycle,
                         String description) {
        super(databaseName, port, user, password, databaseEndpoint, nodeName, standardLifecycle, description);
        
        this.host = (host == null) ? MysqlDbmsRequirement.builder().build() : host;

        requirements.add(this.host);
    }

    /**
     @param nodeName         {@link #nodeName}
     @param databaseName     {@link #databaseEndpoint}
     @param databaseEndpoint {@link #databaseEndpoint}
     */
    public static MysqlDatabaseBuilder builder(String nodeName,
                                               String databaseName,
                                               DatabaseEndpointCapability databaseEndpoint) {
        return (MysqlDatabaseBuilder) new MysqlDatabaseBuilder()
            .nodeName(nodeName)
            .databaseName(databaseName)
            .databaseEndpoint(databaseEndpoint);
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }

    public static class MysqlDatabaseBuilder extends DatabaseBuilder {
    }
}
