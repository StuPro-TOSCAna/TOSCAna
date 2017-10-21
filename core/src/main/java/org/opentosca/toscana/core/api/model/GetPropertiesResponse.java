package org.opentosca.toscana.core.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.opentosca.toscana.core.api.TransformationController;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;


public class GetPropertiesResponse extends ResourceSupport {

    @JsonIgnore
    private String csarName;
    @JsonIgnore
    private String platformName;
    private List<PropertyWrap> properties;

    public GetPropertiesResponse(
        String csarName,
        String platformName,
        @JsonProperty("properties") List<PropertyWrap> properties
    ) {
        this.csarName = csarName;
        this.platformName = platformName;
        this.properties = properties;
        add(linkTo(methodOn(TransformationController.class)
            .getTransformationProperties(csarName, platformName))
            .withSelfRel().expand(csarName)
        );
    }

    public List<PropertyWrap> getProperties() {
        return properties;
    }

    public static class PropertyWrap {
        private String key;
        private String type;

        public PropertyWrap(
            @JsonProperty("key") String key,
            @JsonProperty("type") String type
        ) {
            this.key = key;
            this.type = type;
        }

        @JsonProperty("key")
        public String getKey() {
            return key;
        }

        @JsonProperty("type")
        public String getType() {
            return type;
        }
    }
}
