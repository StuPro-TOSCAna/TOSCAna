package org.opentosca.toscana.model.operation;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.DescribableEntity;
import org.opentosca.toscana.model.artifact.Artifact;
import org.opentosca.toscana.model.util.ToscaKey;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 see TOSCA Specification V1.1 ch. 3.5.13
 */
@EqualsAndHashCode
@ToString
public class Operation extends DescribableEntity {

    /**
     inputs which shall be injected into the artifacts environment before execution.
     Might be empty.
     */
    public static final ToscaKey<OperationVariable> INPUTS = new ToscaKey<>("inputs")
        .type(OperationVariable.class);
    /**
     outputs which are supposed to be available in the environment after execution of the operation.
     Might be empty.
     */
    public static final ToscaKey<OperationVariable> OUTPUTS = new ToscaKey<>("outputs")
        .type(OperationVariable.class);
    /**
     The optional primary deployment artifact.
     */
    public static ToscaKey<Void> IMPLEMENTATION = new ToscaKey<>("implementation")
        .type(Void.class);

    public static final ToscaKey<Artifact> PRIMARY = new ToscaKey<>(IMPLEMENTATION, "primary")
        .type(Artifact.class);
    /**
     several dependent or secondary implementation artifact names which
     are referenced by the primary implementation artifact
     (e.g., a library the script installs or a secondary script).
     Might be empty
     */
    public static final ToscaKey<String> DEPENDENCIES = new ToscaKey<>(IMPLEMENTATION, "dependencies");

    public Operation(MappingEntity entity) {
        super(entity);
    }

    /**
     @return {@link #IMPLEMENTATION}
     */

    public Optional<Artifact> getArtifact() {
        return Optional.ofNullable(get(PRIMARY));
    }

    public Operation setArtifact(Artifact artifact) {
        set(PRIMARY, artifact);
        return this;
    }

    /**
     @return {@link #DEPENDENCIES}
     */
    public Set<String> getDependencies() {
        return new HashSet<>(getCollection(DEPENDENCIES));
    }

    /**
     @return {@link #INPUTS}
     */
    public Set<OperationVariable> getInputs() {
        return new HashSet<>(getCollection(INPUTS));
    }

    /**
     @return {@link #OUTPUTS}
     */
    public Set<OperationVariable> getOutputs() {
        return new HashSet<>(getCollection(OUTPUTS));
    }
}
