package org.opentosca.toscana.model.node;

import java.util.Optional;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.capability.EndpointCapability;
import org.opentosca.toscana.model.capability.PublicEndpointCapability;
import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.relation.DependsOn;
import org.opentosca.toscana.model.requirement.NetworkRequirement;
import org.opentosca.toscana.model.util.RequirementKey;
import org.opentosca.toscana.model.util.ToscaKey;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 Represents logical function that be used in conjunction with a Floating Address
 to distribute an application’s traffic across a number of instances of the application
 (e.g., for a clustered or scaled application).
 (TOSCA Simple Profile in YAML Version 1.1, p.177)
 */
@EqualsAndHashCode
@ToString
public class LoadBalancer extends RootNode {

    /**
     The optional algorithm for this load balancer. Experimental.
     */
    public static ToscaKey<String> ALGORITHM = new ToscaKey<>(PROPERTIES, "algorithm");
    public static ToscaKey<PublicEndpointCapability> CLIENT = new ToscaKey<>(CAPABILITIES, "client")
        .type(PublicEndpointCapability.class);
    public static ToscaKey<NetworkRequirement> APPLICATION = new RequirementKey<>("application")
        .subTypes(EndpointCapability.class, RootNode.class, DependsOn.class)
        .type(NetworkRequirement.class);

    public LoadBalancer(MappingEntity mappingEntity) {
        super(mappingEntity);
        init();
    }

    private void init() {
        setDefault(CLIENT, (PublicEndpointCapability) new PublicEndpointCapability(getChildEntity(CLIENT)).setOccurrence(Range.ANY));
        setDefault(APPLICATION, new NetworkRequirement(getChildEntity(APPLICATION)));
    }

    /**
     @return {@link #CLIENT}
     */
    public PublicEndpointCapability getClient() {
        return get(CLIENT);
    }

    /**
     Sets {@link #CLIENT}
     */
    public LoadBalancer setClient(PublicEndpointCapability client) {
        set(CLIENT, client);
        return this;
    }

    /**
     @return {@link #APPLICATION}
     */
    public NetworkRequirement getApplication() {
        return get(APPLICATION);
    }

    /**
     Sets {@link #APPLICATION}
     */
    public LoadBalancer setApplication(NetworkRequirement application) {
        set(APPLICATION, application);
        return this;
    }

    /**
     @return {@link #ALGORITHM}
     */
    public Optional<String> getAlgorithm() {
        return Optional.ofNullable(get(ALGORITHM));
    }

    /**
     Sets {@link #ALGORITHM}
     */
    public LoadBalancer setAlgorithm(String algorithm) {
        set(ALGORITHM, algorithm);
        return this;
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }
}
