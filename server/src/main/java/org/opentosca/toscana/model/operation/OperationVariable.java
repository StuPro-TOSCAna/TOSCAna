package org.opentosca.toscana.model.operation;

import java.util.Optional;

import org.opentosca.toscana.core.parse.model.ScalarEntity;

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
        return Optional.ofNullable(backingEntity.getValue());
    }

    public void setValue(String value) {
        backingEntity.setValue(value);
    }

    public String getKey() {
        return backingEntity.getName();
    }
}
