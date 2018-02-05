package org.opentosca.toscana.core.transformation.properties;

import org.opentosca.toscana.core.transformation.properties.validators.BooleanValidator;
import org.opentosca.toscana.core.transformation.properties.validators.FloatValidator;
import org.opentosca.toscana.core.transformation.properties.validators.IntegerValidator;
import org.opentosca.toscana.core.transformation.properties.validators.StringValidator;
import org.opentosca.toscana.core.transformation.properties.validators.ValueValidator;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PropertyType {
    NAME("name", String.class, new StringValidator("([a-z])+((-)*([a-z]))*")),
    TEXT("text", String.class, new StringValidator()),
    SECRET("secret", String.class, new StringValidator()),
    INTEGER("integer", Integer.class, new IntegerValidator(false)),
    UNSIGNED_INTEGER("unsigned_integer", Integer.class, new IntegerValidator(true)),
    FLOAT("float", Double.class, new FloatValidator()),
    BOOLEAN("boolean", Boolean.class, new BooleanValidator()),
    INVALID_KEY("invalid_key", String.class, new StringValidator());

    private final ValueValidator valueValidator;
    private final String typeName;
    private final Class<?> outputClass;

    PropertyType(String typeName, Class<?> outputClass, ValueValidator valueValidator) {
        this.valueValidator = valueValidator;
        this.typeName = typeName;
        this.outputClass = outputClass;
    }

    public boolean validate(String input) {
        return valueValidator.isValid(input);
    }

    @JsonValue
    public String getTypeName() {
        return typeName;
    }

    public Class<?> getOutputClass() {
        return outputClass;
    }
}
