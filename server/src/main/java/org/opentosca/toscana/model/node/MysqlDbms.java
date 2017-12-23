package org.opentosca.toscana.model.node;

import java.util.Set;

import org.opentosca.toscana.model.capability.Capability;
import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.datatype.Credential;
import org.opentosca.toscana.model.nodedefinition.BaseDefinition;
import org.opentosca.toscana.model.nodedefinition.MysqlDbmsDefinition;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.relation.HostedOn;
import org.opentosca.toscana.model.requirement.HostRequirement;
import org.opentosca.toscana.model.requirement.Requirement;
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
    private MysqlDbms(Requirement<ContainerCapability, Compute, HostedOn> host,
                      ContainerCapability containerHost,
                      String rootPassword,
                      Integer port,
                      String componentVersion,
                      Credential adminCredential,
                      String nodeName,
                      StandardLifecycle standardLifecycle,
                      Set<Requirement> requirements,
                      Set<Capability> capabilities,
                      String description) {
        super(HostRequirement.getFallback(host), makeValidContainerHost(containerHost), rootPassword, fallbackPort(port),
            componentVersion, adminCredential, nodeName, standardLifecycle, requirements, capabilities, description);
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

    @Override
    protected BaseDefinition getDefinition() {
        return new MysqlDbmsDefinition();
    }

    public static class MysqlDbmsBuilder extends DbmsBuilder {
        protected Set<Requirement> requirements = super.requirements;
        protected Set<Capability> capabilities = super.capabilities;
    }
}
