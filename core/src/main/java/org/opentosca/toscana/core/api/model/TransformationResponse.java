package org.opentosca.toscana.core.api.model;

import java.io.IOException;

import org.opentosca.toscana.core.api.PlatformController;
import org.opentosca.toscana.core.api.TransformationController;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.core.Relation;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Relation(collectionRelation = "transformation")
public class TransformationResponse extends ResourceSupport {
    private final int progress;
    private final String status;
    private final String platform;

    public TransformationResponse(
        @JsonProperty("progress") int progress,
        @JsonProperty("status") String status,
        @JsonProperty("platform") String platform,
        String csarName
    ) {
        this.progress = progress;
        this.status = status;
        this.platform = platform;
        this.add(linkTo(methodOn(TransformationController.class)
            .getCSARTransformation(csarName, platform))
            .withSelfRel().expand(csarName));
        this.add(linkTo(methodOn(TransformationController.class)
            .getTransformationLogs(csarName, platform, 0L))
            .withRel("logs").expand(csarName));
        this.add(linkTo(methodOn(PlatformController.class)
            .getPlatform(platform)).withRel("platform"));
        // TODO FIX
        this.add(linkTo(methodOn(TransformationController.class)
            .getTransformationProperties(csarName, platform))
            .withRel("properties").expand(csarName));
        this.add(linkTo(methodOn(TransformationController.class)
            .deleteTransformation(csarName, platform))
            .withRel("delete").expand(csarName));
        try {
            this.add(linkTo(methodOn(TransformationController.class)
                .getTransformationArtifact(csarName, platform, null))
                .withRel("artifact").expand(csarName));
        } catch (IOException e) {
            //Never happens because the call above is a dummy call
        }
    }

    @JsonProperty("progress")
    public int getProgress() {
        return progress;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("platform")
    public String getPlatform() {
        return platform;
    }
}
