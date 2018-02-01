package org.opentosca.toscana.retrofit.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransformationProperty {
    
    @JsonProperty("key")
    private String key;
    @JsonProperty("type")
    private String type;
    @JsonProperty("value")
    private String value;
    @JsonProperty("description")
    private String description;
    @JsonProperty("required")
    private boolean required;
    @JsonProperty("default")
    private String defaultValue;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
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

    @Override
    public String toString() {
        return String.format("Property (key=%s, type=%s, description=%s, required=%s, value=%s)"
            , key, type, description, required, value);
    }
}
