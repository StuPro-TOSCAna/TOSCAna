package org.opentosca.toscana.retrofit.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public class TransformationProperty {

    @JsonProperty("key")
    private String key;
    @JsonProperty("type")
    private PropertyType type;
    @JsonProperty("value")
    private String value;
    @JsonProperty("description")
    private String description;
    @JsonProperty("required")
    private boolean required;
    @JsonProperty("default_value")
    private String defaultValue;
    @JsonProperty("valid")
    private boolean valid;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public PropertyType getType() {
        return type;
    }

    public void setType(PropertyType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public String toString() {
        return String.format("Property (key=%s, type=%s, description=%s, required=%s, value=%s, valid=%s)"
            , key, type, description, required, value, valid);
    }

    public enum PropertyType {
        NAME("name"),
        TEXT("text"),
        SECRET("secret"),
        INTEGER("integer"),
        UNSIGNED_INTEGER("unsigned_integer"),
        FLOAT("float"),
        BOOLEAN("boolean"),
        INVALID_KEY("invalid_key");

        private final String name;

        PropertyType(String name) {
            this.name = name;
        }

        @JsonValue
        public String getName() {
            return name;
        }
    }
}
