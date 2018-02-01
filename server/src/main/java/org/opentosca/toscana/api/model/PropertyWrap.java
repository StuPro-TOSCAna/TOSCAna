package org.opentosca.toscana.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class PropertyWrap {
    protected final String key;
    protected final String type;
    protected final String description;
    protected final boolean required;
    protected final String value;
    protected final String defaultValue;

    public PropertyWrap(
        @JsonProperty("key") String key,
        @JsonProperty("type") String type,
        @JsonProperty("description") String description,
        @JsonProperty("value") String value,
        @JsonProperty("required") boolean required,
        @JsonProperty("default") String defaultValue
    ) {
        this.key = key;
        this.type = type;
        this.description = description;
        this.required = required;
        this.value = value;
        this.defaultValue = defaultValue;
    }

    @ApiModelProperty(
        required = true,
        notes = "the unique key for a property",
        example = "docker_registry"
    )
    @JsonProperty("key")
    public String getKey() {
        return key;
    }

    @ApiModelProperty(
        required = true,
        notes = "The 'Datatype' of a property. Supported Types are 'name', 'text', 'boolean', " +
            "'integer', 'unsigned_integer', 'float' and 'secret'. If you try to set a value of a non existant key." +
            " The type in the response will be 'invalid' however this type only occurs in that case",
        example = "text"
    )
    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @ApiModelProperty(
        required = true,
        notes = "The description that should be displayed on the Frontend to describe the key",
        example = "The URL to the docker registry"
    )
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @ApiModelProperty(
        required = true,
        notes = "Determines if the property is required to start the transformation",
        example = "true"
    )
    @JsonProperty("required")
    public boolean isRequired() {
        return required;
    }

    @ApiModelProperty(
        required = false,
        notes = "the current value of this property",
        value = "gcr.io"
    )
    @JsonProperty("value")
    public String getValue() {
        return value;
    }

    @ApiModelProperty(
        required = true,
        notes = "the default value of the property"
    )
    @JsonProperty("default")
    public String getDefaultValue() {
        return defaultValue;
    }
}
