package org.opentosca.toscana.model.node;

import java.util.Objects;
import java.util.Optional;

import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.requirement.HostRequirement;
import org.opentosca.toscana.model.requirement.Requirement;
import org.opentosca.toscana.model.datatype.Credential;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.relation.HostedOn;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.Builder;
import lombok.Data;

/**
 Represents a generic software component that can be managed and run by a Compute Node.
 (TOSCA Simple Profile in YAML Version 1.1, p.170)
 */
@Data
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

    private final HostRequirement host;

    @Builder
    protected SoftwareComponent(String componentVersion,
                                Credential adminCredential,
                                HostRequirement host,
                                String nodeName,
                                StandardLifecycle standardLifecycle,
                                String description) {
        super(nodeName, standardLifecycle, description);
        this.componentVersion = componentVersion;
        this.adminCredential = adminCredential;
        this.host = Objects.requireNonNull(host);

        requirements.add(host);
    }

    /**
     @param nodeName {@link #nodeName}
     @param host     {@link #host}
     */
    public static SoftwareComponentBuilder builder(String nodeName,
                                                   HostRequirement host) {
        return new SoftwareComponentBuilder()
            .nodeName(nodeName)
            .host(host);
    }

    public Optional<String> getComponentVersion() {
        return Optional.ofNullable(componentVersion);
    }

    public Optional<Credential> getAdminCredential() {
        return Optional.ofNullable(adminCredential);
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }
}
