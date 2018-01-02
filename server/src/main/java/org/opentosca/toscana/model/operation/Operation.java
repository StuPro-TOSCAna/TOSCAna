package org.opentosca.toscana.model.operation;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.model.DescribableEntity;
import org.opentosca.toscana.model.artifact.Artifact;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

/**
 see TOSCA Specification V1.1 ch. 3.5.13
 */
@Data
public class Operation extends DescribableEntity {

    /**
     The optional primary deployment artifact.
     */
    private final Artifact artifact;

    /**
     several dependent or secondary implementation artifact names which
     are referenced by the primary implementation artifact
     (e.g., a library the script installs or a secondary script).
     Might be empty
     */
    private final Set<String> dependencies;

    /**
     inputs which shall be injected into the artifacts environment before execution.
     Might be empty.
     */
    private final Set<OperationVariable> inputs;

    /**
     outputs which are supposed to be available in the environment after execution of the operation.
     Might be empty.
     */
    private final Set<OperationVariable> outputs;

    @Builder
    protected Operation(Artifact artifact,
                        @Singular Set<String> dependencies,
                        @Singular Set<OperationVariable> inputs,
                        @Singular Set<OperationVariable> outputs,
                        String description) {
        super(description);
        this.artifact = artifact;
        this.dependencies = Objects.requireNonNull(dependencies);
        this.inputs = Objects.requireNonNull(inputs);
        this.outputs = Objects.requireNonNull(outputs);
    }

    /**
     @return {@link #artifact}
     */
    public Optional<Artifact> getArtifact() {
        return Optional.ofNullable(artifact);
    }

    public static class OperationBuilder extends DescribableEntityBuilder {
    }
}
