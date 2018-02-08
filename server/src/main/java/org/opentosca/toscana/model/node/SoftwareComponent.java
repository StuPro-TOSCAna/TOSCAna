package org.opentosca.toscana.model.node;

import java.util.Optional;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.datatype.Credential;
import org.opentosca.toscana.model.relation.HostedOn;
import org.opentosca.toscana.model.requirement.HostRequirement;
import org.opentosca.toscana.model.util.RequirementKey;
import org.opentosca.toscana.model.util.ToscaKey;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

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
    public static ToscaKey<String> COMPONENT_VERSION = new ToscaKey<>(PROPERTIES, "component_version");

    /**
     The optional credential that can be used to authenticate to the software component.
     (TOSCA Simple Profile in YAML Version 1.1, p.170)
     */
    public static ToscaKey<Credential> ADMIN_CREDENTIAL = new ToscaKey<>(PROPERTIES, "admin_credential")
        .type(Credential.class);

    public static ToscaKey<HostRequirement> HOST = new RequirementKey<>("host")
        .subTypes(ContainerCapability.class, Compute.class, HostedOn.class)
        .type(HostRequirement.class);

    public SoftwareComponent(MappingEntity mappingEntity) {
        super(mappingEntity);
        setDefault(HOST, new HostRequirement(getChildEntity(HOST)));
    }

    /**
     @return {@link #HOST}
     */
    public HostRequirement getHost() {
        return get(HOST);
    }

    /**
     Sets {@link #HOST}
     */
    public SoftwareComponent setHost(HostRequirement host) {
        set(HOST, host);
        return this;
    }

    /**
     @return {@link #COMPONENT_VERSION}
     */
    public Optional<String> getComponentVersion() {
        return Optional.ofNullable(get(COMPONENT_VERSION));
    }

    /**
     Sets {@link #COMPONENT_VERSION}
     */
    public SoftwareComponent setComponentVersion(String componentVersion) {
        set(COMPONENT_VERSION, componentVersion);
        return this;
    }

    /**
     @return {@link #ADMIN_CREDENTIAL}
     */
    public Optional<Credential> getAdminCredential() {
        return Optional.ofNullable(get(ADMIN_CREDENTIAL));
    }

    /**
     Sets {@link #ADMIN_CREDENTIAL}
     */
    public SoftwareComponent setAdminCredential(Credential adminCredential) {
        set(ADMIN_CREDENTIAL, adminCredential);
        return this;
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }
}

