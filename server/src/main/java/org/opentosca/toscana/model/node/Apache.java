package org.opentosca.toscana.model.node;

import java.util.Set;

import org.opentosca.toscana.model.capability.AdminEndpointCapability;
import org.opentosca.toscana.model.capability.Capability;
import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.capability.EndpointCapability;
import org.opentosca.toscana.model.datatype.Credential;
import org.opentosca.toscana.model.nodedefinition.AbstractDefinition;
import org.opentosca.toscana.model.nodedefinition.ApacheDefinition;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.relation.HostedOn;
import org.opentosca.toscana.model.requirement.HostRequirement;
import org.opentosca.toscana.model.requirement.Requirement;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 (TOSCA Simple Profile in YAML Version 1.1, p. 222)
 */
@EqualsAndHashCode
@ToString
public class Apache extends WebServer {

    @Builder
    protected Apache(String componentVersion,
                     Credential adminCredential,
                     Requirement<ContainerCapability, Compute, HostedOn> host,
                     ContainerCapability containerHost,
                     EndpointCapability databaseEndpoint,
                     AdminEndpointCapability adminEndpoint,
                     String nodeName,
                     StandardLifecycle lifecycle,
                     Set<Requirement> requirements,
                     Set<Capability> capabilities,
                     String description) {
        super(componentVersion, adminCredential, HostRequirement.getFallback(host), containerHost,
            databaseEndpoint, adminEndpoint, nodeName, lifecycle, requirements, capabilities, description);
    }

    /**
     @param nodeName {@link #nodeName}
     */
    public static ApacheBuilder builder(String nodeName) {
        return new ApacheBuilder()
            .nodeName(nodeName);
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }

    public static class ApacheBuilder extends WebServerBuilder {
        protected Set<Requirement> requirements = super.requirements;
        protected Set<Capability> capabilities = super.capabilities;
    }

    @Override
    protected AbstractDefinition getDefinition() {
        return new ApacheDefinition();
    }
}
