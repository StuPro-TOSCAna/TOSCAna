package org.opentosca.toscana.api.model;

import java.util.List;

import org.opentosca.toscana.api.docs.HiddenResourceSupport;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class PropertiesResponse extends HiddenResourceSupport {

    private final List<PropertyWrap> properties;

    public PropertiesResponse(
        @JsonProperty("properties") List<PropertyWrap> properties
    ) {
        this.properties = properties;
    }

    @ApiModelProperty(
        required = true,
        notes = "The list of properties associated with this transformation."
    )
    @JsonProperty("properties")
    public List<PropertyWrap> getProperties() {
        return properties;
    }
}
