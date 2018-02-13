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

    /**
     Use when the variable's key differs from the scalar entity's parent name (e.g., when the value is a function)
     */
    public OperationVariable(ScalarEntity entity, @Nullable String name) {
        this.backingEntity = entity;
        this.name = name;
    }

    /**
     Use when the variable's key is the scalar entity's parent name
     */
    public OperationVariable(ScalarEntity entity) {
        this(entity, entity.getParent().get().getName());
    }

    public Optional<String> getValue() {
        return Optional.ofNullable(backingEntity.getValue());
    }

    public void setValue(String value) {
        backingEntity.setValue(value);
    }

    public String getKey() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("OperationVariable [key='%s', value='%s']", getKey(), getValue());
    }
}
