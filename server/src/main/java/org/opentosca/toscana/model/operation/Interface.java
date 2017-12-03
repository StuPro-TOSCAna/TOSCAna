package org.opentosca.toscana.model.operation;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

/**
 Defines a named interface that can be associated with a Node.
 (TOSCA Simple Profile in YAML Version 1.1, p. 73)
 */
@Data
public class Interface {

    /**
     Set of inputs which the TOSCA orchestrator shall make available for all defined operations.
     This means the contained inputs shall be made accessible to the implementation
     artifacts associated to each operation during their execution.
     (TOSCA Simple Profile in YAML Version 1.1, p. 74)
     */
    private final Set<OperationVariable> inputs;

    /**
     Represents the defined operations of this interface.
     */
    private final Set<Operation> operations;

    @Builder
    protected Interface(@Singular Set<OperationVariable> inputs,
                        @Singular Set<Operation> operations) {
        this.inputs = Objects.requireNonNull(inputs);
        this.operations = operations.stream().filter(Objects::nonNull).collect(Collectors.toSet());
    }
}
