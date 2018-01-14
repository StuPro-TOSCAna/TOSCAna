package org.opentosca.toscana.model.node;

import org.opentosca.toscana.core.parse.graphconverter.MappingEntity;
import org.opentosca.toscana.model.requirement.ContainerHostRequirement;
import org.opentosca.toscana.model.requirement.NetworkRequirement;
import org.opentosca.toscana.model.requirement.StorageRequirement;
import org.opentosca.toscana.model.util.ToscaKey;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 Represents an application that requires Container-level virtualization technology.
 (TOSCA1.1, p. 177)
 */
@EqualsAndHashCode
@ToString
public class ContainerApplication extends RootNode {

    public static ToscaKey<ContainerHostRequirement> HOST = new ToscaKey<>(REQUIREMENTS, "host")
        .type(ContainerHostRequirement.class);
    public static ToscaKey<StorageRequirement> STORAGE = new ToscaKey<>(REQUIREMENTS, "storage")
        .type(StorageRequirement.class);
    public static ToscaKey<NetworkRequirement> NETWORK = new ToscaKey<>(REQUIREMENTS, "network")
        .type(NetworkRequirement.class);

    public ContainerApplication(MappingEntity mappingEntity) {
        super(mappingEntity);
        init();
    }

    private void init() {
        setDefault(STORAGE, new StorageRequirement(getChildEntity(STORAGE)));
        setDefault(NETWORK, new NetworkRequirement(getChildEntity(NETWORK)));
        setDefault(HOST, new ContainerHostRequirement(getChildEntity(HOST)));
    }

    /**
     @return {@link #HOST}
     */
    public ContainerHostRequirement getHost() {
        return get(HOST);
    }

    /**
     Sets {@link #HOST}
     */
    public ContainerApplication setHost(ContainerHostRequirement host) {
        set(HOST, host);
        return this;
    }

    /**
     @return {@link #STORAGE}
     */
    public StorageRequirement getStorage() {
        return get(STORAGE);
    }

    /**
     Sets {@link #STORAGE}
     */
    public ContainerApplication setStorage(StorageRequirement storage) {
        set(STORAGE, storage);
        return this;
    }

    /**
     @return {@link #NETWORK}
     */
    public NetworkRequirement getNetwork() {
        return get(NETWORK);
    }

    /**
     Sets {@link #NETWORK}
     */
    public ContainerApplication setNetwork(NetworkRequirement network) {
        set(NETWORK, network);
        return this;
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }
}
