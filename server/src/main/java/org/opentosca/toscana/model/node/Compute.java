package org.opentosca.toscana.model.node;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.model.capability.AdminEndpointCapability;
import org.opentosca.toscana.model.capability.BindableCapability;
import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.model.capability.ScalableCapability;
import org.opentosca.toscana.model.datatype.NetworkInfo;
import org.opentosca.toscana.model.datatype.PortInfo;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.requirement.BlockStorageRequirement;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.Builder;
import lombok.Data;

/**
 Represents one or more real or virtual processors of software applications or services along with other essential local
 resources.
 Collectively, the resources the compute node represents can logically be viewed as a (real or virtual) “server”.
 (TOSCA Simple Profile in YAML Version 1.1, p. 169)
 */
@Data
public class Compute extends RootNode {
    /**
     The optional primary private IP address assigned by the cloud provider that applications may use to access the
     Compute node.
     (TOSCA Simple Profile in YAML Version 1.1, p. 169)
     */
    private final String privateAddress;

    /**
     The optional primary public IP address assigned by the cloud provider that applications may use to access the
     Compute node.
     (TOSCA Simple Profile in YAML Version 1.1, p. 169)
     */
    private final String publicAddress;

    /**
     The collection of logical networks assigned to the compute host instance and information about them.
     (TOSCA Simple Profile in YAML Version 1.1, p. 169)
     */
    private final Set<NetworkInfo> networks = new HashSet<>();

    /**
     The set of logical ports assigned to this compute host instance and information about them.
     (TOSCA Simple Profile in YAML Version 1.1, p. 169)
     */
    private final Set<PortInfo> ports = new HashSet<>();

    private final ContainerCapability host;

    private final OsCapability os;

    private final AdminEndpointCapability adminEndpoint;

    private final ScalableCapability scalable;

    private final BindableCapability binding;

    private final BlockStorageRequirement localStorage;

    @Builder
    protected Compute(String privateAddress,
                      String publicAddress,
                      ContainerCapability host,
                      OsCapability os,
                      AdminEndpointCapability adminEndpoint,
                      ScalableCapability scalable,
                      BindableCapability binding,
                      BlockStorageRequirement localStorage,
                      String nodeName,
                      StandardLifecycle standardLifecycle,
                      String description) {
        super(nodeName, standardLifecycle, description);
        this.privateAddress = privateAddress;
        this.publicAddress = publicAddress;
        this.os = Objects.requireNonNull(os);
        this.adminEndpoint = Objects.requireNonNull(adminEndpoint);
        this.host = (host == null) ? ContainerCapability.builder().build() : host;
        this.scalable = (scalable == null) ? ScalableCapability.builder().build() : scalable;
        this.binding = (binding == null) ? BindableCapability.builder().build() : binding;
        this.localStorage = Objects.requireNonNull(localStorage);

        capabilities.add(this.host);
        capabilities.add(this.os);
        capabilities.add(this.adminEndpoint);
        capabilities.add(this.scalable);
        capabilities.add(this.binding);
        requirements.add(this.localStorage);
    }

    /**
     @param nodeName      {@link #nodeName}
     @param adminEndpoint {@link #adminEndpoint}
     @param localStorage  {@link #localStorage}
     */
    public static ComputeBuilder builder(String nodeName,
                                         OsCapability os,
                                         AdminEndpointCapability adminEndpoint,
                                         BlockStorageRequirement localStorage) {
        return new ComputeBuilder()
            .os(os)
            .nodeName(nodeName)
            .adminEndpoint(adminEndpoint)
            .localStorage(localStorage);
    }

    /**
     @return {@link #privateAddress}
     */
    public Optional<String> getPrivateAddress() {
        return Optional.ofNullable(privateAddress);
    }

    /**
     @return {@link #publicAddress}
     */
    public Optional<String> getPublicAddress() {
        return Optional.ofNullable(publicAddress);
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }

    public static class ComputeBuilder extends RootNodeBuilder {
    }
}

