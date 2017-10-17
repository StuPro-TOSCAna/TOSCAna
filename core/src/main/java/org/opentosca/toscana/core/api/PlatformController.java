package org.opentosca.toscana.core.api;

import org.opentosca.toscana.core.api.model.PlatformResponse;
import org.opentosca.toscana.core.transformation.Platform;
import org.opentosca.toscana.core.util.PlatformProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * This REST Controller handles every request
 * to list the supported Platforms and to retrieve information about a specific platform.
 * <p>
 * For sample Responses of the Requests, please have a look at docs/api/api_samples.md
 */
@CrossOrigin
@RestController
@RequestMapping("/platforms")
public class PlatformController {

	@Autowired
	public PlatformProvider platformProvider;

	/**
	 * Lists all Supported Platforms (HTTP Response Method)
	 * <p>
	 * It handles the <code>/platforms</code> Request
	 * <p>
	 * Always responds with HTTP-Code 200 (application/hal+json)
	 */
	@RequestMapping(
		path = "",
		method = RequestMethod.GET
	)
	public ResponseEntity<ResourceSupport> getPlatforms() {
		Link selfLink = linkTo(methodOn(PlatformController.class).getPlatforms()).withSelfRel();
		ArrayList<PlatformResponse> responses = new ArrayList<>();
		for (Platform platform : platformProvider.getSupportedPlatforms()) {
			PlatformResponse res = getPlatformResource(platform);
			responses.add(res);
		}
		Resources<PlatformResponse> resources = new Resources<>(responses, selfLink);

		return ResponseEntity.ok(resources);
	}

	/**
	 * Returns the information for a specific platform.
	 * <p>
	 * This method handles the <code>/platforms/{id}</code> request
	 * <p>
	 * Responds with Http code 200 normaly (application/hal+json) and
	 * with code 404 (no contents) if the plaform with the given name (case sensitive) does not exist.
	 *
	 * @param id the <code>id</code> (identifier) of the platform (HTTP Path Parameter)
	 */
	@RequestMapping(
		path = "/{id}",
		method = RequestMethod.GET
	)
	public ResponseEntity<PlatformResponse> getPlatform(
		@PathVariable(name = "id") String id
	) {
		Platform p = platformProvider.findById(id);
		if (p == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(getPlatformResource(p));
	}

	/**
	 * Generates a Platform Response and adds a self link to it.
	 */
	private PlatformResponse getPlatformResource(Platform platform) {
		PlatformResponse res = new PlatformResponse(platform);
		res.add(linkTo(methodOn(PlatformController.class).getPlatform(res.getIdentifier())).withSelfRel());
		return res;
	}
}
