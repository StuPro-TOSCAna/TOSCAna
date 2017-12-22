package org.opentosca.toscana.model.node;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.model.capability.AdminEndpointCapability;
import org.opentosca.toscana.model.capability.AttachmentCapability;
import org.opentosca.toscana.model.capability.BindableCapability;
import org.opentosca.toscana.model.capability.Capability;
import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.model.capability.ScalableCapability;
import org.opentosca.toscana.model.datatype.NetworkInfo;
import org.opentosca.toscana.model.datatype.PortInfo;
import org.opentosca.toscana.model.nodedefinition.AbstractDefinition;
import org.opentosca.toscana.model.nodedefinition.ComputeDefinition;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.relation.AttachesTo;
import org.opentosca.toscana.model.requirement.BlockStorageRequirement;
import org.opentosca.toscana.model.requirement.Requirement;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.Builder;
import lombok.Data;

import static org.opentosca.toscana.model.nodedefinition.ComputeDefinition.NETWORKS_PROPERTY;
import static org.opentosca.toscana.model.nodedefinition.ComputeDefinition.PORTS_PROPERTY;
import static org.opentosca.toscana.model.nodedefinition.ComputeDefinition.PRIVATE_ADDRESS_PROPERTY;
import static org.opentosca.toscana.model.nodedefinition.ComputeDefinition.PUBLIC_ADDRESS_PROPERTY;

/**
 Represents one or more real or virtual processors of software applications or services along with other essential local
 resources.
 Collectively, the resources the compute node represents can logically be viewed as a (real or virtual) “server”.
 (TOSCA Simple Profile in YAML Version 1.1, p. 169)
 */
@Data
public class Compute extends RootNode {
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
    private final AdminEndpointCapability endpoint;
    private final ScalableCapability scalable;
    private final BindableCapability binding;
    private final Requirement<AttachmentCapability, BlockStorage, AttachesTo> localStorage;
    /**
     The optional primary private IP address assigned by the cloud provider that applications may use to access the
     Compute node.
     (TOSCA Simple Profile in YAML Version 1.1, p. 169)
     */
    private String privateAddress;
    /**
     The optional primary public IP address assigned by the cloud provider that applications may use to access the
     Compute node.
     (TOSCA Simple Profile in YAML Version 1.1, p. 169)
     */
    private String publicAddress;

    @Builder
    protected Compute(String privateAddress,
                      String publicAddress,
                      ContainerCapability host,
                      OsCapability os,
                      AdminEndpointCapability endpoint,
                      ScalableCapability scalable,
                      BindableCapability binding,
                      Requirement<AttachmentCapability, BlockStorage, AttachesTo> localStorage,
                      String nodeName,
                      StandardLifecycle standardLifecycle,
                      Set<Requirement> requirements,
                      Set<Capability> capabilities,
                      String description) {
        super(nodeName, standardLifecycle, requirements, capabilities, description);
        this.privateAddress = privateAddress;
        this.publicAddress = publicAddress;
        this.os = OsCapability.getFallback(os);
        this.endpoint = AdminEndpointCapability.getFallback(endpoint);
        this.host = (host == null) ? ContainerCapability.builder().build() : host;
        this.scalable = (scalable == null) ? ScalableCapability.builder().build() : scalable;
        this.binding = (binding == null) ? BindableCapability.builder().build() : binding;
        this.localStorage = BlockStorageRequirement.getFallback(localStorage);

        this.capabilities.add(this.host);
        this.capabilities.add(this.os);
        this.capabilities.add(this.endpoint);
        this.capabilities.add(this.scalable);
        this.capabilities.add(this.binding);
        this.requirements.add(this.localStorage);
    }

    /**
     @param nodeName {@link #nodeName}
     */
    public static ComputeBuilder builder(String nodeName) {
        return new ComputeBuilder()
            .nodeName(nodeName);
    }

    /**
     @return {@link #privateAddress}
     */
    public Optional<String> getPrivateAddress() {
        return Optional.ofNullable(get(PRIVATE_ADDRESS_PROPERTY));
    }

    public void setPrivateAddress(String privateAddress) {
        set(PRIVATE_ADDRESS_PROPERTY, privateAddress);
    }

    /**
     @return {@link #publicAddress}
     */
    public Optional<String> getPublicAddress() {
        return Optional.ofNullable(get(PUBLIC_ADDRESS_PROPERTY));
    }

    public void setPublicAddress(String publicAddress) {
        set(PUBLIC_ADDRESS_PROPERTY, publicAddress);
    }

    public Set<NetworkInfo> getNetworks() {
        return get(NETWORKS_PROPERTY);
    }

    public Set<PortInfo> getPorts() {
        return get(PORTS_PROPERTY);
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }

    @Override
    protected AbstractDefinition getDefinition() {
        return new ComputeDefinition();
    }

    public static class ComputeBuilder extends RootNodeBuilder {
        protected Set<Requirement> requirements = super.requirements;
        protected Set<Capability> capabilities = super.capabilities;
    }
}

