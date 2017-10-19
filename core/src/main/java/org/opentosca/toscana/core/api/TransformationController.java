package org.opentosca.toscana.core.api;

import org.opentosca.toscana.core.api.model.*;
import org.opentosca.toscana.core.api.model.GetPropertiesResponse.PropertyWrap;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarService;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.logging.LogEntry;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.TransformationService;
import org.opentosca.toscana.core.transformation.TransformationState;
import org.opentosca.toscana.core.transformation.artifacts.TargetArtifact;
import org.opentosca.toscana.core.transformation.properties.Property;
import org.opentosca.toscana.core.transformation.platform.PlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * This controller implements API operations regarding Transformations
 * 
 * For sample Responses of the Requests, please have a look at docs/api/api_samples.md
 */
@CrossOrigin
@RestController
@RequestMapping("/csars/{csarName}/transformations")
public class TransformationController {

	@Autowired
	public CsarService csarService;
	@Autowired
	public TransformationService transformationService;
	@Autowired
	public PlatformService platformService;

	/**
	 * This Request Returns a list of all transformations belonging to a csar
	 * <p>
	 * Response Codes:
	 * 200 - Operation was Performed Sucessfuly
	 * 404 - Csar not found
	 * <p>
	 * Response Content Types for Code:
	 * 200 - (application/hal+json)
	 * 404 - Can be ignored, empty body
	 */
	@RequestMapping(
		path = "",
		method = RequestMethod.GET,
		produces = "application/hal+json"
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

	/**
	 * Returns informations about a single transformation
	 * <p>
	 * Response Codes:
	 * 200 - Operation was Performed Sucessfuly
	 * 404 - Csar not found, the platform does not exist or no transformation for the specific platform was found for this csar
	 * <p>
	 * Response Content Types for Code:
	 * 200 - (application/hal+json)
	 * 404 - Can be ignored, empty body
	 */
	@RequestMapping(
		path = "/{platform}",
		method = RequestMethod.GET,
		produces = "application/hal+json"
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

	/**
	 * Creates a new transformation for the given platform and csar
	 * <p>
	 * Response Codes:
	 * 200 - Creation was sucessfull
	 * 400 - This csar already has a transformation for this platform
	 * 404 - Csar not found or the platform does not exist
	 * <p>
	 * Response Content Types for Code:
	 * 200 - (application/hal+json)
	 * 400 - Can be ignored, empty body
	 * 404 - Can be ignored, empty body
	 */
	@RequestMapping(
		path = "/{platform}/create",
		method = {RequestMethod.POST, RequestMethod.PUT},
		produces = "application/hal+json"
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
		Platform p = platformService.findById(platform);
		if (p == null) {
			return ResponseEntity.notFound().build();
		}
		transformationService.createTransformation(csar, p);
		return ResponseEntity.ok().build();
	}

	/**
	 * Deletes a transformation. If it is still running, the job will be canceled!
	 * <p>
	 * Response Codes:
	 * 200 - Operation was Performed Sucessfuly
	 * 404 - Csar not found, the platform does not exist or no transformation for the specific platform was found for this csar
	 * <p>
	 * Response Content Types for Code:
	 * 200 - Can be ignored, empty body
	 * 404 - Can be ignored, empty body
	 */
	@RequestMapping(
		path = "/{platform}/delete",
		method = RequestMethod.DELETE,
		produces = "application/hal+json"
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

	/**
	 * Returns the logs from a given start index to the current end of the log file.
	 * If the start index is higher then the current end index, a empty list is returned!
	 * <p>
	 * Response Codes:
	 * 200 - Operation was Performed Sucessfuly
	 * 404 - Csar not found, the platform does not exist or no transformation for the specific platform was found for this csar
	 * <p>
	 * Response Content Types for Code:
	 * 200 - application/hal+json
	 * 404 - Can be ignored, empty body
	 */
	@RequestMapping(
		path = "/{platform}/logs",
		method = RequestMethod.GET,
		produces = "application/hal+json"
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

	/**
	 * Returns the URL to download the target Artifact
	 * <p>
	 * Response Codes:
	 * 200 - Operation was Performed Sucessfuly
	 * 404 - Csar not found,
	 * the platform does not exist, the transformation has not finished yet
	 * or no transformation for the specific platform was found for this csar
	 * <p>
	 * Response Content Types for Code:
	 * 200 - application/hal+json
	 * 404 - Can be ignored, empty body
	 */
	@RequestMapping(
		path = "/{platform}/artifact",
		method = RequestMethod.GET,
		produces = "application/hal+json"
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

	/**
	 * Returns a list of properties that have to be entered in order for the transformator to proceed with the transformation
	 * This only happens, if the transfomation is in the "INPUT_REUQIRED" state otherwise this will return with error code 400
	 * <p>
	 * Response Codes:
	 * 200 - Operation was Performed Sucessfuly
	 * 400 - The Transformation is not in the INPUT_REQUIRED State
	 * 404 - Csar not found,
	 * the platform does not exist,
	 * or no transformation for the specific platform was found for this csar
	 * <p>
	 * Response Content Types for Code:
	 * 200 - application/hal+json
	 * 400 - Can be ignored, empty body
	 * 404 - Can be ignored, empty body
	 */
	@RequestMapping(
		path = "/{platform}/properties",
		method = RequestMethod.GET,
		produces = "application/hal+json"
	)
	public ResponseEntity<GetPropertiesResponse> getTransformationProperties(
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
		} else if (transformation.getState() != TransformationState.INPUT_REQUIRED) {
			return ResponseEntity.badRequest().build();
		}
		List<PropertyWrap> propertyWrapList = new ArrayList<>();
		//TODO add filtering depending on the transformation state (i.e. Transforming, Deploying...)
		for (Property property : transformation.getPlatform().getProperties()) {
			propertyWrapList.add(new PropertyWrap(property.getKey(), property.getType().getTypeName()));
		}
		return ResponseEntity.ok(new GetPropertiesResponse(name, platform, propertyWrapList));
	}


	/**
	 * This operation is used to "enter" the required inputs for this a json body, containing the values defined by the user,
	 * gets POSTed or PUTed. Once received, the server checks the inputs if they are valid they get set. At the end a json Map is retuned.
	 * This map shows which values have been set an which ones have failed
	 * <p>
	 * Response Codes:
	 * 200 - Operation was Performed Sucessfuly (All properties that should have been set by this call have been set successfully)
	 * 406 - At least one property value was not set, because the key does not exist or the given value was invalid
	 * 400 - The Transformation is not in the INPUT_REQUIRED State
	 * 404 - Csar not found,
	 * the platform does not exist,
	 * or no transformation for the specific platform was found for this csar
	 * <p>
	 * Response Content Types for Code:
	 * 200 - application/json
	 * 400 - Can be ignored, empty body
	 * 404 - Can be ignored, empty body
	 * 406 - application/json
	 */
	@RequestMapping(
		path = "/{platform}/properties",
		method = {RequestMethod.POST, RequestMethod.PUT},
		produces = "application/json"
	)
	public ResponseEntity<SetPropertiesResponse> setTransformationProperties(
		@PathVariable(name = "csarName") String name,
		@PathVariable(name = "platform") String platform,
		@RequestBody SetPropertiesRequest setPropertiesRequest
	) {
		Csar csar = findCsarByName(name);
		if (csar == null) {
			return ResponseEntity.notFound().build();
		}
		Transformation transformation = csar.getTransformations().get(platform);
		if (transformation == null) {
			return ResponseEntity.notFound().build();
		} else if (transformation.getState() != TransformationState.INPUT_REQUIRED) {
			return ResponseEntity.badRequest().build();
		}
		Map<String, Boolean> sucesses = new HashMap<>();
		boolean somethingFailed = false;
		//Set The Properties and check their validity
		for (Map.Entry<String, String> entry : setPropertiesRequest.getProperties().entrySet()) {
			try {
				transformation.setProperty(entry.getKey(), entry.getValue());
				sucesses.put(entry.getKey(), true);
			} catch (Exception e) {
				somethingFailed = true;
				sucesses.put(entry.getKey(), false);
			}
		}
		//Return the result (with code 200 if all inputs were valid and 400 if at least 1 was invalid)
		if (!somethingFailed) {
			return ResponseEntity.ok(new SetPropertiesResponse(sucesses));
		} else {
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new SetPropertiesResponse(sucesses));
		}
	}

	private Csar findCsarByName(String name) {
		return csarService.getCsar(name);
	}
}
