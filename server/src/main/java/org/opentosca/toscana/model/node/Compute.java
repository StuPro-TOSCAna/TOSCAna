package org.opentosca.toscana.model.node;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.capability.AdminEndpointCapability;
import org.opentosca.toscana.model.capability.AttachmentCapability;
import org.opentosca.toscana.model.capability.BindableCapability;
import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.model.capability.ScalableCapability;
import org.opentosca.toscana.model.datatype.NetworkInfo;
import org.opentosca.toscana.model.datatype.PortInfo;
import org.opentosca.toscana.model.relation.AttachesTo;
import org.opentosca.toscana.model.requirement.BlockStorageRequirement;
import org.opentosca.toscana.model.util.RequirementKey;
import org.opentosca.toscana.model.util.ToscaKey;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 Represents one or more real or virtual processors of software applications or services along with other essential local
 resources.
 Collectively, the resources the compute node represents can logically be viewed as a (real or virtual) “server”.
 (TOSCA Simple Profile in YAML Version 1.1, p. 169)
 */
@EqualsAndHashCode
@ToString
public class Compute extends RootNode {

    /**
     The optional primary private IP address assigned by the cloud provider that applications may use to access the
     Compute node.
     (TOSCA Simple Profile in YAML Version 1.1, p. 169)
     */
    public static ToscaKey<String> PRIVATE_ADDRESS = new ToscaKey<>(ATTRIBUTES, "private_address");

    /**
     The optional primary public IP address assigned by the cloud provider that applications may use to access the
     Compute node.
     (TOSCA Simple Profile in YAML Version 1.1, p. 169)
     */
    public static ToscaKey<String> PUBLIC_ADDRESS = new ToscaKey<>(ATTRIBUTES, "public_address");

    /**
     The collection of logical networks assigned to the compute host instance and information about them.
     (TOSCA Simple Profile in YAML Version 1.1, p. 169)
     */
    public static ToscaKey<NetworkInfo> NETWORKS = new ToscaKey<>(ATTRIBUTES, "networks");

    /**
     The set of logical ports assigned to this compute host instance and information about them.
     (TOSCA Simple Profile in YAML Version 1.1, p. 169)
     */
    public static ToscaKey<PortInfo> PORTS = new ToscaKey<>(ATTRIBUTES, "ports");

    public static ToscaKey<ContainerCapability> HOST = new ToscaKey<>(CAPABILITIES, "host")
        .type(ContainerCapability.class);
    public static ToscaKey<OsCapability> OS = new ToscaKey<>(CAPABILITIES, "os")
        .type(OsCapability.class);
    public static ToscaKey<AdminEndpointCapability> ENDPOINT = new ToscaKey<>(CAPABILITIES, "endpoint")
        .type(AdminEndpointCapability.class);
    public static ToscaKey<ScalableCapability> SCALABLE = new ToscaKey<>(CAPABILITIES, "scalable")
        .type(ScalableCapability.class);
    public static ToscaKey<BindableCapability> BINDING = new ToscaKey<>(CAPABILITIES, "binding")
        .type(BindableCapability.class);
    public static ToscaKey<BlockStorageRequirement> LOCAL_STORAGE = new RequirementKey<>("local_storage")
        .subTypes(AttachmentCapability.class, BlockStorage.class, AttachesTo.class)
        .type(BlockStorageRequirement.class);

    public Compute(MappingEntity mappingEntity) {
        super(mappingEntity);
        init();
    }

    private void init() {
        setDefault(HOST, new ContainerCapability(getChildEntity(HOST)));
        setDefault(OS, new OsCapability(getChildEntity(OS)));
        setDefault(ENDPOINT, new AdminEndpointCapability(getChildEntity(ENDPOINT)));
        setDefault(SCALABLE, new ScalableCapability(getChildEntity(SCALABLE)));
        setDefault(BINDING, new BindableCapability(getChildEntity(BINDING)));
        setDefault(LOCAL_STORAGE, new BlockStorageRequirement(getChildEntity(LOCAL_STORAGE)));
    }

    /**
     @return {@link #PRIVATE_ADDRESS}
     */
    public Optional<String> getPrivateAddress() {
        return Optional.ofNullable(get(PRIVATE_ADDRESS));
    }

    /**
     Sets {@link #PRIVATE_ADDRESS}
     */
    public Compute setPrivateAddress(String privateAddress) {
        set(PRIVATE_ADDRESS, privateAddress);
        return this;
    }

    /**
     @return {@link #PUBLIC_ADDRESS}
     */
    public Optional<String> getPublicAddress() {
        return Optional.ofNullable(get(PUBLIC_ADDRESS));
    }

    /**
     Sets {@link #PUBLIC_ADDRESS}
     */
    public Compute setPublicAddress(String publicAddress) {
        set(PUBLIC_ADDRESS, publicAddress);
        return this;
    }

    /**
     @return {@link #NETWORKS}
     */
    public Set<NetworkInfo> getNetworks() {
        return new HashSet<>(getCollection(NETWORKS));
    }

    /**
     @return {@link #PORTS}
     */
    public Set<PortInfo> getPorts() {
        return new HashSet<>(getCollection(PORTS));
    }

    /**
     @return {@link #HOST}
     */
    public ContainerCapability getHost() {
        return get(HOST);
    }

    /**
     Sets {@link #HOST}
     */
    public Compute setHost(ContainerCapability host) {
        set(HOST, host);
        return this;
    }

    /**
     @return {@link #OS}
     */
    public OsCapability getOs() {
        return get(OS);
    }

    /**
     Sets {@link #OS}
     */
    public Compute setOs(OsCapability os) {
        set(OS, os);
        return this;
    }

    /**
     @return {@link #ENDPOINT}
     */
    public AdminEndpointCapability getEndpoint() {
        return get(ENDPOINT);
    }

    /**
     Sets {@link #ENDPOINT}
     */
    public Compute setEndpoint(AdminEndpointCapability endpoint) {
        set(ENDPOINT, endpoint);
        return this;
    }

    /**
     @return {@link #SCALABLE}
     */
    public ScalableCapability getScalable() {
        return get(SCALABLE);
    }

    /**
     Sets {@link #SCALABLE}
     */
    public Compute setScalable(ScalableCapability scalable) {
        set(SCALABLE, scalable);
        return this;
    }

    /**
     @return {@link #BINDING}
     */
    public BindableCapability getBinding() {
        return get(BINDING);
    }

    /**
     Sets {@link #BINDING}
     */
    public Compute setBinding(BindableCapability binding) {
        set(BINDING, binding);
        return this;
    }

    /**
     @return {@link #LOCAL_STORAGE}
     */
    public BlockStorageRequirement getLocalStorage() {
        return get(LOCAL_STORAGE);
    }

    /**
     Sets {@link #LOCAL_STORAGE}
     */
    public Compute setLocalStorage(BlockStorageRequirement localStorage) {
        set(LOCAL_STORAGE, localStorage);
        return this;
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }
}

