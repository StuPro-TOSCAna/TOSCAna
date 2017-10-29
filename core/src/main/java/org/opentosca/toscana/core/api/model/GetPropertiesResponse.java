package org.opentosca.toscana.core.api.model;

import java.util.List;

import org.opentosca.toscana.core.api.TransformationController;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

public class GetPropertiesResponse extends ResourceSupport {

    private List<PropertyWrap> properties;

    public GetPropertiesResponse(
        String csarName,
        String platformName,
        @JsonProperty("properties") List<PropertyWrap> properties
    ) {
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
        private String description;
        private boolean required;
        private String value;

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

        @JsonProperty("key")
        public String getKey() {
            return key;
        }

        @JsonProperty("type")
        public String getType() {
            return type;
        }

        @JsonProperty("description")
        public String getDescription() {
            return description;
        }

        @JsonProperty("required")
        public boolean isRequired() {
            return required;
        }

        @JsonProperty("value")
        public String getValue() {
            return value;
        }
    }
}
