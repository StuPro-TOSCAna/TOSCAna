package org.opentosca.toscana.api.model;

import org.opentosca.toscana.core.transformation.properties.InputProperty;
import org.opentosca.toscana.core.transformation.properties.PropertyType;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class InputWrap extends OutputWrap {

    protected final boolean required;
    protected final String defaultValue;
    protected boolean valid;

    public InputWrap(
        @JsonProperty("key") String key,
        @JsonProperty("type") PropertyType type,
        @JsonProperty("description") String description,
        @JsonProperty("value") String value,
        @JsonProperty("required") boolean required,
        @JsonProperty("default_value") String defaultValue,
        @JsonProperty("valid") boolean valid) {
        super(key, value, type, description);
        this.required = required;
        this.defaultValue = defaultValue;
        this.valid = valid;
    }

    public InputWrap(InputProperty input) {
        this(input.getKey(),
            input.getType(),
            input.getDescription().orElse(null),
            input.getValue().orElse(null),
            input.isRequired(),
            input.getDefaultValue().orElse(null),
            input.isValid());
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
        required = true,
        notes = "the default value of the property"
    )
    @JsonProperty("default_value")
    public String getDefaultValue() {
        return defaultValue;
    }

    @ApiModelProperty(
        notes = "true if the property is valid, false otherwise. The server will ignore this value, as it is designed " +
            "to be read-only."
    )
    @JsonProperty("valid")
    public boolean isValid() {
        return valid;
    }
}
