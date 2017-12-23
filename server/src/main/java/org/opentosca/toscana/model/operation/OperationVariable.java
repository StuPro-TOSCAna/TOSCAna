package org.opentosca.toscana.model.operation;

import java.util.Optional;

import org.opentosca.toscana.model.ToscaEntity;

import lombok.Data;

/**
 See TOSCA Specification V1.1 ch. 2.1.1; 2.14
 <p>
 Serves either as input or output variable for an operation.
 */
@Data
public class OperationVariable extends ToscaEntity {
    
    public final static String VALUE_PROPERTY = "value";

    private final String key;

    private String value;

    public OperationVariable(String key) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("key must not be empty or null");
        }
        this.key = key;
    }

    public OperationVariable(String key, String value) throws IllegalArgumentException {
        this(key);
        this.value = value;
    }

    /**
     @return {@link #value}
     */
    public Optional<String> getValue() {
        return Optional.ofNullable(get(VALUE_PROPERTY));
    }

    public void setValue(String value) {
        set(VALUE_PROPERTY, value);
    }
}
