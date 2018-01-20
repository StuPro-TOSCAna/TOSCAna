package org.opentosca.toscana.core.transformation.properties;

import java.util.Optional;

import lombok.Data;

@Data
public class PlatformProperty implements Property {

    private final String key;
    private final PropertyType type;
    private final String description;
    private final boolean required;
    private final String defaultValue;
    private String value;

    /**
     Constructs a new property object, describing the name of a property and the type of the value.

     @param key         the unique name of the property
     @param type        the expected data type of the property value
     @param description a short description of the property (should not exceed 200 characters, does not get checked tough
     @param required    determines if the property is required or not
     */
    public PlatformProperty(
        String key,
        PropertyType type,
        String description,
        boolean required
    ) {
        this(key, type, description, required, null);
    }

    public PlatformProperty(
        String key,
        PropertyType type,
        String description,
        boolean required,
        String defaultValue
    ) {
        this.key = key;
        this.type = type;
        this.description = description;
        this.required = required;
        this.defaultValue = defaultValue;
        setValue(defaultValue);
    }

    /**
     This constructor creates a SimpleProperty with no description and the created property is required

     @param key  the unique name of the property
     @param type the expected data type of the property value
     */
    public PlatformProperty(String key, PropertyType type) {
        this(key, type, "", true);
    }

    @Override
    public Optional<String> getValue() {
        return Optional.ofNullable(value);
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    @Override
    public Optional<String> getDefaultValue() {
        return Optional.ofNullable(defaultValue);
    }

    public PlatformProperty copy() {
        PlatformProperty p = new PlatformProperty(
            key,
            type,
            description,
            required,
            defaultValue
        );
        p.setValue(value);
        return p;
    }
}
