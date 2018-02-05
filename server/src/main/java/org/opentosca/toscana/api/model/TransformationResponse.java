package org.opentosca.toscana.api.model;

import java.io.IOException;
import java.util.List;

import org.opentosca.toscana.api.PlatformController;
import org.opentosca.toscana.api.TransformationController;
import org.opentosca.toscana.api.docs.HiddenResourceSupport;
import org.opentosca.toscana.core.plugin.lifecycle.LifecyclePhase;
import org.opentosca.toscana.core.transformation.TransformationState;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.hateoas.core.Relation;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@ApiModel
@Relation(collectionRelation = "transformation")
public class TransformationResponse extends HiddenResourceSupport {
    private final List<? extends LifecyclePhase> phases;
    private final TransformationState state;
    private final String platform;

    public TransformationResponse(
        @JsonProperty("phases") List<? extends LifecyclePhase> phases,
        @JsonProperty("state") TransformationState state,
        @JsonProperty("platform") String platform,
        String csarName
    ) {
        this.phases = phases;
        this.state = state;
        this.platform = platform;
        this.add(ControllerLinkBuilder.linkTo(methodOn(TransformationController.class)
            .getCSARTransformation(csarName, platform))
            .withSelfRel().expand(csarName));
        this.add(linkTo(methodOn(TransformationController.class)
            .getTransformationLogs(csarName, platform, 0L))
            .withRel("logs").expand(csarName));
        this.add(ControllerLinkBuilder.linkTo(methodOn(PlatformController.class)
            .getPlatform(platform)).withRel("platform"));
        this.add(linkTo(methodOn(TransformationController.class)
            .getInputs(csarName, platform))
            .withRel("inputs").expand(csarName));
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

    @ApiModelProperty(
        required = true,
        notes = "The phases of the transformation"
    )
    @JsonProperty("phases")
    public List<? extends LifecyclePhase> getPhases() {
        return phases;
    }

    @ApiModelProperty(
        required = true,
        //Sadly Java does not allow Dynamic calls here, therefore this has to be updated manually
        notes = "The Current State of the transformation. Has to be one of the following: " +
            "\"READY\", \"INPUT_REQUIRED\", \"TRANSFORMING\", \"DONE\" or \"ERROR\"",
        example = "READY"
    )
    @JsonProperty("state")
    public TransformationState getState() {
        return state;
    }

    @ApiModelProperty(
        required = true,
        notes = "The platform identifier for this transformation",
        example = "kubernetes"
    )
    @JsonProperty("platform")
    public String getPlatform() {
        return platform;
    }
}
