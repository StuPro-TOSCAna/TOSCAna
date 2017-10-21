package org.opentosca.toscana.core.api;

import org.opentosca.toscana.core.api.exceptions.PlatformNotFoundException;
import org.opentosca.toscana.core.api.model.PlatformResponse;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.platform.PlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private Logger log = LoggerFactory.getLogger(getClass());

    private final PlatformService platformService;

    @Autowired
    public PlatformController(PlatformService platformService) {
        this.platformService = platformService;
    }

    /**
     * Lists all Supported Platforms (HTTP Response Method)
     * <p>
     * It handles the <code>/platforms</code> Request
     * <p>
     * Always responds with HTTP-Code 200 (application/hal+json)
     */
    @RequestMapping(
        path = "",
        method = RequestMethod.GET,
        produces = "application/hal+json"
    )
    public ResponseEntity<ResourceSupport> getPlatforms() {
        Link selfLink = linkTo(methodOn(PlatformController.class).getPlatforms()).withSelfRel();
        ArrayList<PlatformResponse> responses = new ArrayList<>();
        for (Platform platform : platformService.getSupportedPlatforms()) {
            log.info("Adding Platform {} to response ", platform.id);
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
        method = RequestMethod.GET,
        produces = "application/hal+json"
    )
    public ResponseEntity<PlatformResponse> getPlatform(
        @PathVariable(name = "id") String id
    ) {
        Platform p = platformService.findById(id);
        if (p == null) {
            throw new PlatformNotFoundException();
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
