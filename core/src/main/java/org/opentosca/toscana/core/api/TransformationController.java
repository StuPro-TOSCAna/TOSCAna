package org.opentosca.toscana.core.api;

import org.opentosca.toscana.core.api.model.ArtifactResponse;
import org.opentosca.toscana.core.api.model.LogResponse;
import org.opentosca.toscana.core.api.model.TransformationResponse;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarService;
import org.opentosca.toscana.core.logging.Log;
import org.opentosca.toscana.core.logging.LogEntry;
import org.opentosca.toscana.core.transformation.Platform;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.TransformationService;
import org.opentosca.toscana.core.transformation.artifacts.TargetArtifact;
import org.opentosca.toscana.core.util.PlatformProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/csars/{csarName}/transformations")
public class TransformationController {

	@Autowired
	public CsarService csarService;
	@Autowired
	public TransformationService transformationService;
	@Autowired
	public PlatformProvider platformProvider;

	@RequestMapping(
		path = "",
		method = RequestMethod.GET
	)
	public ResponseEntity<Resources<TransformationResponse>> getCSARTransformations(
		@PathVariable(name = "csarName") String name
	) {
		Csar csar = findCsarByName(name);
		if (csar == null) {
			return ResponseEntity.notFound().build();
		}
		Link selfLink =
			linkTo(methodOn(TransformationController.class)
				.getCSARTransformations(name))
				.withSelfRel()
				.expand(name);
		List<TransformationResponse> transformations = new ArrayList<>();
		for (Map.Entry<String, Transformation> entry : csar.getTransformations().entrySet()) {
			transformations.add(new TransformationResponse(
				0,
				entry.getValue().getState().name(),
				entry.getKey(),
				csar.getIdentifier()
			));
		}
		Resources<TransformationResponse> resources = new Resources<>(transformations, selfLink);
		return ResponseEntity.ok(resources);
	}

	@RequestMapping(
		path = "/{platform}",
		method = RequestMethod.GET
	)
	public ResponseEntity<TransformationResponse> getCSARTransformation(
		@PathVariable(name = "csarName") String name,
		@PathVariable(name = "platform") String platform
	) {
		Csar csar = findCsarByName(name);
		if (csar == null) {
			return ResponseEntity.notFound().build();
		}
		Transformation transformation = csar.getTransformations().get(platform);
		if (transformation == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok().body(new TransformationResponse(
			0,
			transformation.getState().name(),
			platform, name
		));
	}

	@RequestMapping(
		path = "/{platform}/create",
		method = {RequestMethod.POST, RequestMethod.PUT}
	)
	public ResponseEntity<TransformationResponse> addTransformation(
		@PathVariable(name = "csarName") String name,
		@PathVariable(name = "platform") String platform
	) {
		Csar csar = findCsarByName(name);
		if (csar == null) {
			return ResponseEntity.notFound().build();
		}
		//Return bad Request if a transformation for this platform is already present
		if (csar.getTransformations().get(platform) != null) {
			return ResponseEntity.badRequest().build();
		}
		//Return 404 if the platform does not exist
		Platform p = platformProvider.findById(platform);
		if (p == null) {
			return ResponseEntity.notFound().build();
		}
		transformationService.createTransformation(csar, p);
		return ResponseEntity.ok().build();
	}

	@RequestMapping(
		path = "/{platform}/delete",
		method = RequestMethod.DELETE
	)
	public ResponseEntity<TransformationResponse> deleteTransformation(
		@PathVariable(name = "csarName") String name,
		@PathVariable(name = "platform") String platform
	) {
		Csar csar = findCsarByName(name);
		if (csar == null) {
			return ResponseEntity.notFound().build();
		}
		Transformation transformation = csar.getTransformations().get(platform);
		if (transformation == null) {
			return ResponseEntity.notFound().build();
		}
		if (transformationService.deleteTransformation(transformation)) {
			// return 200 if the deletion was sucessful
			return ResponseEntity.ok().build();
		} else {
			// return 500 if the deletion failed
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@RequestMapping(
		path = "/{platform}/logs",
		method = RequestMethod.GET
	)
	public ResponseEntity<LogResponse> getTransformationLogs(
		@PathVariable(name = "csarName") String name,
		@PathVariable(name = "platform") String platform,
		@RequestParam(name = "start", required = false, defaultValue = "0") Long start
	) {
		Csar csar = findCsarByName(name);
		if (csar == null) {
			return ResponseEntity.notFound().build();
		}
		Transformation transformation = csar.getTransformations().get(platform);
		if (transformation == null) {
			return ResponseEntity.notFound().build();
		}
		Log log = transformation.getLog();
		List<LogEntry> entries = log.getLogEntries(Math.toIntExact(start));
		return ResponseEntity.ok().body(new LogResponse(
			start,
			start + entries.size() - 1, //TODO Maybe move this calculation into the LogResponse Class
			entries,
			platform,
			name
		));
	}

	@RequestMapping(
		path = "/{platform}/artifact",
		method = RequestMethod.GET
	)
	public ResponseEntity<ArtifactResponse> getTransformationArtifact(
		@PathVariable(name = "csarName") String name,
		@PathVariable(name = "platform") String platform
	) {
		Csar csar = findCsarByName(name);
		if (csar == null) {
			return ResponseEntity.notFound().build();
		}
		Transformation transformation = csar.getTransformations().get(platform);
		if (transformation == null) {
			return ResponseEntity.notFound().build();
		}
		TargetArtifact artifact = transformation.getTargetArtifact();
		if (artifact == null) {
			//The Target Artifact is not existing, that means the transformation has not finished!
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok().body(new ArtifactResponse(artifact.getArtifactDownloadURL(), platform, name));
	}

	@RequestMapping(
		path = "/{platform}/properties",
		method = {RequestMethod.POST, RequestMethod.PUT}
	)
	public ResponseEntity<String> setTransformationProperties(
		@PathVariable(name = "csarName") String name,
		@PathVariable(name = "platform") String platform
	) {
		Csar csar = findCsarByName(name);
		if (csar == null) {
			return ResponseEntity.notFound().build();
		}
		Transformation transformation = csar.getTransformations().get(platform);
		if (transformation == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok().build();
	}


	@RequestMapping(
		path = "/{platform}/properties",
		method = RequestMethod.GET
	)
	public ResponseEntity<String> getTransformationProperties(
		@PathVariable(name = "csarName") String name,
		@PathVariable(name = "platform") String platform
	) {
		Csar csar = findCsarByName(name);
		if (csar == null) {
			return ResponseEntity.notFound().build();
		}
		Transformation transformation = csar.getTransformations().get(platform);
		if (transformation == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok().build();
	}

	private Csar findCsarByName(String name) {
		Csar csar = null;
		Collection<Csar> csars = csarService.getCsars();
		for (Csar c : csars) {
			if (c.getIdentifier().equals(name)) {
				csar = c;
				break;
			}
		}
		return csar;
	}
}
