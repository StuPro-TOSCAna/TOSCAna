package org.opentosca.toscana.core.api;

import org.opentosca.toscana.core.api.model.PlatformResponse;
import org.opentosca.toscana.core.transformation.Platform;
import org.opentosca.toscana.core.util.PlatformProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * This REST Controller handles every request
 * to list the supported Platforms and to retrieve information about a specific platform.
 */
@RestController
@RequestMapping("/platforms")
public class PlatformController {

	@Autowired
	public PlatformProvider platformProvider;

	/**
	 * Lists all Supported Platforms (HTTP Response Method)
	 * <p>
	 * It handles the <code>/platforms</code> Request
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

	private PlatformResponse getPlatformResource(Platform platform) {
		PlatformResponse res = new PlatformResponse(platform);
		res.add(linkTo(methodOn(PlatformController.class).getPlatform(res.getIdentifier())).withSelfRel());
		return res;
	}
}
