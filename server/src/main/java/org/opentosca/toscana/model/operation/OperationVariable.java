package org.opentosca.toscana.model.operation;

import java.util.Optional;

import javax.annotation.Nullable;

import org.opentosca.toscana.core.parse.model.ScalarEntity;

import lombok.EqualsAndHashCode;

/**
 See TOSCA Specification V1.1 ch. 2.1.1; 2.14
 <p>
 Serves either as input or output variable for an operation.
 */
@EqualsAndHashCode
public class OperationVariable {

    private ScalarEntity backingEntity;
    private final String name;

    public OperationVariable(ScalarEntity entity, @Nullable String name) {
        this.backingEntity = entity;
        this.name = name;
    }

    public Optional<String> getValue() {
        return Optional.ofNullable(backingEntity.getValue());
    }

    public void setValue(String value) {
        backingEntity.setValue(value);
    }

    public String getKey() {
        return (name != null) ? name : backingEntity.getName();
    }

    @Override
    public String toString() {
        return String.format("OperationVariable [key='%s', value='%s']", getKey(), getValue());
    }
}
