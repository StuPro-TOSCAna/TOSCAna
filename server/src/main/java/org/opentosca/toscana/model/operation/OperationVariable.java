package org.opentosca.toscana.model.operation;

import java.util.Optional;

import org.opentosca.toscana.core.parse.graphconverter.ScalarEntity;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 See TOSCA Specification V1.1 ch. 2.1.1; 2.14
 <p>
 Serves either as input or output variable for an operation.
 */
@EqualsAndHashCode
@ToString
public class OperationVariable {

    private ScalarEntity backingEntity;

    public OperationVariable(ScalarEntity entity) {
        this.backingEntity = entity;
    }

    public Optional<String> getValue() {
        return Optional.ofNullable(backingEntity.get());
    }

    public void setValue(String value) {
        backingEntity.set(value);
    }

    public String getKey() {
        return backingEntity.getName();
    }
}
