package org.opentosca.toscana.core.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.opentosca.toscana.core.api.TransformationController;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

public class ArtifactResponse extends ResourceSupport {
    private String accessURL;

    public ArtifactResponse(
        @JsonProperty("access_url") String accessURL,
        String platform,
        String csarName
    ) {
        this.accessURL = accessURL;
        add(linkTo(methodOn(TransformationController.class)
            .getTransformationArtifact(csarName, platform)).withSelfRel().expand(csarName));
    }

    @JsonProperty("access_url")
    public String getAccessURL() {
        return accessURL;
    }
}
