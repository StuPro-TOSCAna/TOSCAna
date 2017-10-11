package org.opentosca.toscana.core.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.opentosca.toscana.core.api.PlatformController;
import org.opentosca.toscana.core.api.TransformationController;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.core.Relation;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Relation(collectionRelation = "transformation")
public class TransformationResponse extends ResourceSupport {
	private int progress;
	private String status;
	private String platform;
	private String csarName;

	public TransformationResponse(
		@JsonProperty("progress") int progress,
		@JsonProperty("status") String status,
		String platform,
		String csarName
	) {
		this.progress = progress;
		this.status = status;
		this.csarName = csarName;
		this.platform = platform;
		this.add(linkTo(methodOn(TransformationController.class)
			.getCSARTransformation(csarName,platform))
			.withSelfRel().expand(csarName));
		this.add(linkTo(methodOn(TransformationController.class)
			.getTransformationLogs(csarName,platform, 0L))
			.withRel("logs").expand(csarName));
		this.add(linkTo(methodOn(PlatformController.class)
			.getPlatform(platform)).withRel("platform"));
		this.add(linkTo(methodOn(TransformationController.class)
			.getTransformationArtifact(csarName, platform))
			.withRel("artifact").expand(csarName));
		this.add(linkTo(methodOn(TransformationController.class)
			.getTransformationProperties(csarName, platform))
			.withRel("properties").expand(csarName));
		this.add(linkTo(methodOn(TransformationController.class)
			.deleteTransformation(csarName,platform))
			.withRel("delete").expand(csarName));
	}

	@JsonProperty("progress")
	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	@JsonProperty("status")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
