package org.opentosca.toscana.model.operation;

import java.util.Optional;

import org.opentosca.toscana.core.parse.model.Connection;
import org.opentosca.toscana.core.parse.model.Entity;
import org.opentosca.toscana.core.parse.model.ScalarEntity;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 See TOSCA Specification V1.1 ch. 2.1.1; 2.14
 <p>
 Serves either as input or output variable for an operation.
 */
@EqualsAndHashCode
public class OperationVariable {

    private ScalarEntity backingEntity;
    private String name = null;

    public OperationVariable(ScalarEntity entity, Entity parent){
        this.backingEntity = entity;
        Connection c = backingEntity.getGraph().getEdge(parent, entity);
        if (c != null) {
            name = c.getKey();
        }
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
