package org.opentosca.toscana.model.node;

import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.model.capability.Capability;
import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.datatype.Credential;
import org.opentosca.toscana.model.nodedefinition.AbstractDefinition;
import org.opentosca.toscana.model.nodedefinition.SoftwareComponentDefinition;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.relation.HostedOn;
import org.opentosca.toscana.model.requirement.HostRequirement;
import org.opentosca.toscana.model.requirement.Requirement;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import static org.opentosca.toscana.model.nodedefinition.SoftwareComponentDefinition.ADMIN_CREDENTIAL_PROPERTY;
import static org.opentosca.toscana.model.nodedefinition.SoftwareComponentDefinition.COMPONENT_VERSION_PROPERTY;

/**
 Represents a generic software component that can be managed and run by a Compute Node.
 (TOSCA Simple Profile in YAML Version 1.1, p.170)
 */
@EqualsAndHashCode
@ToString
public class SoftwareComponent extends RootNode {

    /**
     The optional software component’s version.
     (TOSCA Simple Profile in YAML Version 1.1, p.170)
     */
    private final String componentVersion;

    /**
     The optional credential that can be used to authenticate to the software component.
     (TOSCA Simple Profile in YAML Version 1.1, p.170)
     */
    private final Credential adminCredential;

    private final Requirement<ContainerCapability, Compute, HostedOn> host;

    @Builder
    protected SoftwareComponent(String componentVersion,
                                Credential adminCredential,
                                Requirement<ContainerCapability, Compute, HostedOn> host,
                                String nodeName,
                                StandardLifecycle standardLifecycle,
                                Set<Requirement> requirements,
                                Set<Capability> capabilities,
                                String description) {
        super(nodeName, standardLifecycle, requirements, capabilities, description);
        this.componentVersion = componentVersion;
        this.adminCredential = adminCredential;
        this.host = (host == null) ? HostRequirement.builder().build() : host;

        this.requirements.add(this.host);
    }

    /**
     @param nodeName {@link #nodeName}
     */
    public static SoftwareComponentBuilder builder(String nodeName) {
        return new SoftwareComponentBuilder()
            .nodeName(nodeName);
    }

    public Optional<String> getComponentVersion() {
        return Optional.ofNullable(get(COMPONENT_VERSION_PROPERTY));
    }

    public Optional<Credential> getAdminCredential() {
        return Optional.ofNullable(get(ADMIN_CREDENTIAL_PROPERTY));
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }

    @Override
    protected AbstractDefinition getDefinition() {
        return new SoftwareComponentDefinition();
    }

    public static class SoftwareComponentBuilder extends RootNodeBuilder {
        protected Set<Requirement> requirements = super.requirements;
        protected Set<Capability> capabilities = super.capabilities;
    }
}

