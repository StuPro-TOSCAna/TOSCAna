package org.opentosca.toscana.api.model;

import org.opentosca.toscana.core.transformation.properties.OutputProperty;
import org.opentosca.toscana.core.transformation.properties.PropertyType;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

public class OutputWrap {
    protected final String key;
    protected final PropertyType type;
    protected final String description;
    protected final String value;

    public OutputWrap(@JsonProperty("key") String key,
                      @JsonProperty("value") String value,
                      @JsonProperty("type") PropertyType type,
                      @JsonProperty("description") String description) {
        this.key = key;
        this.value = value;
        this.type = type;
        this.description = description;
    }

    public OutputWrap(OutputProperty output) {
        this(output.getKey(), output.getValue().orElse(null), output.getType(), output.getDescription().orElse(null));
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
    public PropertyType getType() {
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
        notes = "the current value of this property",
        value = "gcr.io"
    )
    @JsonProperty("value")
    public String getValue() {
        return value;
    }
}
