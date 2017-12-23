package org.opentosca.toscana.core.transformation.properties;

import org.opentosca.toscana.model.operation.OperationVariable;

import lombok.Data;

@Data
public class Property extends OperationVariable {

    private final PropertyType type;
    private final String description;
    private final boolean required;
    private final String defaultValue;

    /**
     Constructs a new property object, describing the key of a property and the type of the value.

     @param key         the unique key of the property
     @param type        the expected data type of the property value
     @param description a short description of the property (should not exceed 200 characters, does not get checked tough
     @param required    determines if the property is required or not
     */
    public Property(
        String key,
        PropertyType type,
        String description,
        boolean required
    ) {
        this(key, type, description, required, null);
    }

    public Property(
        String key,
        PropertyType type,
        String description,
        boolean required,
        String defaultValue
    ) {
        super(key);
        this.type = type;
        this.description = description;
        this.required = required;
        this.defaultValue = defaultValue;
        setValue(defaultValue);
    }

    /**
     This constructor creates a Property with no description and the created property is required

     @param key  the unique key of the property
     @param type the expected data type of the property value
     */
    public Property(String key, PropertyType type) {
        this(key, type, "", true);
    }
}
