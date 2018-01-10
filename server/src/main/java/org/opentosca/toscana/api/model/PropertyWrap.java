package org.opentosca.toscana.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class PropertyWrap {
    private final String key;
    private final String type;
    private final String description;
    private final boolean required;
    private final String value;

    public PropertyWrap(
        @JsonProperty("key") String key,
        @JsonProperty("type") String type,
        @JsonProperty("description") String description,
        @JsonProperty("value") String value,
        @JsonProperty("required") boolean required
    ) {
        this.key = key;
        this.type = type;
        this.description = description;
        this.required = required;
        this.value = value;
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
        notes = "The \"Datatype\" of a property. Supported Types are \"name\", \"text\", \"boolean\", " +
            "\"integer\", \"unsigned_integer\", \"float\" and \"secret\"",
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
}
