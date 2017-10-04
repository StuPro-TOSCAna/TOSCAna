package org.opentosca.toscana.core.api;

import org.opentosca.toscana.core.api.model.TransformationResponse;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarService;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.TransformationService;
import org.opentosca.toscana.core.util.PlatformProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/csars/{csarName}/transformations")
public class CsarTransformationController {

	@Autowired
	public CsarService csarService;
	@Autowired
	public TransformationService transformationService;
	@Autowired
	public PlatformProvider platformProvider;

	@GetMapping
	public ResponseEntity<Resources<TransformationResponse>> getCSARTransformations(
		@PathVariable(name = "csarName") String name
	) {
		Csar csar = findCsarByName(name);
		if (csar == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok().build();
	}

	@GetMapping
	@RequestMapping("/{platform}")
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
			name,
			platform
		));
	}
	
	@PutMapping
	@RequestMapping("/{platform}/create")
	public ResponseEntity<TransformationResponse> addTransformation(
		@PathVariable(name = "csarName") String name,
		@PathVariable(name = "platform") String platform
	) {
		Csar csar = findCsarByName(name);
		if (csar == null) {
			return ResponseEntity.notFound().build();
		}
		transformationService.createTransformation(csar, platformProvider.findById(platform));
		return ResponseEntity.ok().build();
	}

	@DeleteMapping
	@RequestMapping("/{platform}/delete")
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
		transformationService.deleteTransformation(transformation);
		return ResponseEntity.ok().build();
	}

	@GetMapping
	@RequestMapping("/{platform}/logs")
	public ResponseEntity<String> getTransformationLogs(
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
		return ResponseEntity.ok().build();
	}

	@PostMapping
	@PutMapping
	@RequestMapping("/{platform}/properties/set")
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

	@GetMapping
	@RequestMapping("/{platform}/properties")
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

	@GetMapping
	@RequestMapping("/{platform}/artifact")
	public ResponseEntity<String> getTransformationArtifact(
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
