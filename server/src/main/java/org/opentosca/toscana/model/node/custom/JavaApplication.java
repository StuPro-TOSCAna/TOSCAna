package org.opentosca.toscana.model.node.custom;

import java.util.Optional;

import org.opentosca.toscana.core.parse.ToscaTemplateException;
import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.artifact.Artifact;
import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.node.SoftwareComponent;
import org.opentosca.toscana.model.relation.HostedOn;
import org.opentosca.toscana.model.requirement.HostRequirement;
import org.opentosca.toscana.model.requirement.JavaRuntimeRequirement;
import org.opentosca.toscana.model.util.RequirementKey;
import org.opentosca.toscana.model.util.ToscaKey;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 Represents a java application. A `jar` file must be supplied as deployment artifact.
 */
@EqualsAndHashCode
@ToString
public class JavaApplication extends SoftwareComponent {

    /**
     The options which are supplied to the vm on startup
     */
    public static ToscaKey<String> VM_OPTIONS = new ToscaKey<>(PROPERTIES, "vm_options");

    /**
     The arguments which are supplied to the application on startup
     */
    public static ToscaKey<String> ARGUMENTS = new ToscaKey<>(PROPERTIES, "arguments");

    public static ToscaKey<JavaRuntimeRequirement> HOST = new RequirementKey<>("host")
        .subTypes(ContainerCapability.class, JavaRuntime.class, HostedOn.class);

    public JavaApplication(MappingEntity mappingEntity) {
        super(mappingEntity);
        setDefault(HOST, new JavaRuntimeRequirement(getChildEntity(HOST)));
    }

    public Artifact getJar() {
        return getArtifacts().stream().findAny().orElseThrow(() ->
            new ToscaTemplateException("JavaApplication node '%s' does not have "));
    }

    /**
     @return {@link #VM_OPTIONS}
     */
    public Optional<String> getVmOptions() {
        return Optional.ofNullable(get(VM_OPTIONS));
    }

    /**
     Sets {@link #VM_OPTIONS}
     */
    public JavaApplication setVmOptions(String vmOptions) {
        set(VM_OPTIONS, vmOptions);
        return this;
    }

    /**
     @return {@link #ARGUMENTS}
     */
    public Optional<String> getArguments() {
        return Optional.ofNullable(get(ARGUMENTS));
    }

    /**
     Sets {@link #ARGUMENTS}
     */
    public JavaApplication setArguments(String arguments) {
        set(ARGUMENTS, arguments);
        return this;
    }

    /**
     @return {@link #HOST}
     */
    public JavaRuntimeRequirement getJreHost() {
        return get(HOST);
    }

    /**
     @return {@link #HOST}
     */
    @Override
    public HostRequirement getHost() {
        throw new UnsupportedOperationException("Use 'getJreHost() instead");
    }

    /**
     Sets {@link #HOST}
     */
    public JavaApplication setHost(JavaRuntimeRequirement host) {
        set(HOST, host);
        return this;
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }
}
