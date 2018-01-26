package org.opentosca.toscana.model.operation;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.BaseToscaElement;
import org.opentosca.toscana.model.util.ToscaKey;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 Defines a named interface that can be associated with a Node.
 (TOSCA Simple Profile in YAML Version 1.1, p. 73)
 */
@EqualsAndHashCode
@ToString
public class Interface extends BaseToscaElement {

    /**
     Set of inputs which the TOSCA orchestrator shall make available for all defined operations.
     This means the contained inputs shall be made accessible to the implementation
     artifacts associated to each operation during their execution.
     (TOSCA Simple Profile in YAML Version 1.1, p. 74)
     */
    public static ToscaKey<OperationVariable> INPUTS = new ToscaKey<>("inputs")
        .type(OperationVariable.class);

    public Interface(MappingEntity entity) {
        super(entity);
    }

    /**
     @return {@link #INPUTS}
     */
    public Set<OperationVariable> getInputs() {
        return new HashSet<>(getCollection(INPUTS));
    }

    /**
     Represents the defined operations of this interface.
     */
    public Set<Operation> getOperations() {
        Set<Operation> operations = getThisAsSet(Operation.class);
        /* filter operation set (in tosca, inputs and outputs are unfortunately defined 
         on the same level as the individual operations) */
        operations = operations.stream()
            .filter(o -> !o.getEntityName().matches("inputs|outputs"))
            .collect(Collectors.toSet());
        return operations;
    }
}
