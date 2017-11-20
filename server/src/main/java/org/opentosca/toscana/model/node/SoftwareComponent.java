package org.opentosca.toscana.model.node;

import java.util.Objects;
import java.util.Optional;

import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.capability.Requirement;
import org.opentosca.toscana.model.datatype.Credential;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.relation.HostedOn;
import org.opentosca.toscana.model.visitor.Visitor;

import lombok.Builder;
import lombok.Data;

/**
 Represents a generic software component that can be managed and run by a Compute Node.
 (TOSCA Simple Profile in YAML Version 1.1, p.170)
 */
@Data
public class SoftwareComponent extends RootNode {

    public final Requirement<ContainerCapability, Compute, HostedOn> host;

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

    @Builder
    protected SoftwareComponent(String componentVersion,
                                Credential adminCredential,
                                Requirement<ContainerCapability, Compute, HostedOn> host,
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
     Only use when subclass is shadowing the `host` field.
     */
    protected SoftwareComponent(String componentVersion,
                                Credential adminCredential,
                                String nodeName,
                                StandardLifecycle standardLifecycle,
                                String description) {
        super(nodeName, standardLifecycle, description);
        this.componentVersion = componentVersion;
        this.adminCredential = adminCredential;
        this.host = null; // this is a workaround. field shall not be used because its shadowed by the subclass
    }

    /**
     @param nodeName {@link #nodeName}
     @param host     {@link #host}
     */
    public static SoftwareComponentBuilder builder(String nodeName,
                                                   Requirement<ContainerCapability, Compute, HostedOn> host) {
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
    public void accept(Visitor v) {
        v.visit(this);
    }
}