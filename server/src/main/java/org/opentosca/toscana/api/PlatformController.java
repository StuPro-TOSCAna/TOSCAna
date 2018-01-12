package org.opentosca.toscana.api;

import java.util.ArrayList;
import java.util.Optional;

import org.opentosca.toscana.api.docs.HiddenResources;
import org.opentosca.toscana.api.docs.PlatformResources;
import org.opentosca.toscana.api.docs.RestErrorResponse;
import org.opentosca.toscana.api.exceptions.PlatformNotFoundException;
import org.opentosca.toscana.api.model.PlatformResponse;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.platform.PlatformService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 This REST Controller handles every request to list the supported Platforms and to retrieve information about a
 specific platform.
 <p>
 For sample Responses of the Requests, please have a look at docs/api/api_samples.md
 */
@CrossOrigin
@RestController
@RequestMapping("/api/platforms")
@Api(
    tags = {"platforms"},
    value = "/platforms",
    description = "Operations for the supported platforms of the transformer"
)
public class PlatformController {

    private final static Logger logger = LoggerFactory.getLogger(PlatformController.class);

    private final PlatformService platformService;

    @Autowired
    public PlatformController(PlatformService platformService) {
        this.platformService = platformService;
    }

    /**
     Lists all Supported Platforms (HTTP Response Method).
     <p>
     It handles the <code>/platforms</code> Request
     <p>
     Always responds with HTTP-Code 200 (application/hal+json)
     */
    @ApiOperation(
        value = "List all supported Platforms",
        notes = "Returns a HAL resource (_embedded) containing all " +
            "Platforms supported by this transformer",
        code = 200,
        response = PlatformResources.class
    )
    @RequestMapping(
        path = "",
        method = RequestMethod.GET,
        produces = "application/hal+json"
    )
    public ResponseEntity<Resources<PlatformResponse>> getPlatforms() {
        Link selfLink = linkTo(methodOn(PlatformController.class).getPlatforms()).withSelfRel();
        ArrayList<PlatformResponse> responses = new ArrayList<>();
        for (Platform platform : platformService.getSupportedPlatforms()) {
            logger.info("Adding Platform {} to response ", platform.id);
            PlatformResponse res = getPlatformResource(platform);
            responses.add(res);
        }
        Resources<PlatformResponse> resources = new HiddenResources<>(responses, selfLink);

        return ResponseEntity.ok(resources);
    }

    /**
     Returns the information for a specific platform.
     <p>
     This method handles the <code>/platforms/{id}</code> request
     <p>
     jResponds with Http code 200 normally (application/hal+json) and with code 404 (application/hal+json, standard
     error message) if the platform with the given name (case sensitive) does not exist.

     @param id the <code>id</code> (identifier) of the platform (HTTP Path Parameter)
     */
    @ApiOperation(
        value = "Get the Details for a specific Platform",
        notes = "Returns the resource object for one specific plugin (platform)",
        code = 200
    )
    @ApiResponses({
        @ApiResponse(
            code = 200,
            message = "The request has been executed with no error!",
            response = PlatformResponse.class
        ),
        @ApiResponse(
            code = 404,
            message = "There is no platform with the given name",
            response = RestErrorResponse.class
        )
    })
    @RequestMapping(
        path = "/{id}",
        method = RequestMethod.GET,
        produces = "application/hal+json"
    )
    public ResponseEntity<PlatformResponse> getPlatform(
        @ApiParam(name = "id", value = "The Platform identifier", required = true)
        @PathVariable(name = "id") String id
    ) {
        Optional<Platform> optionalPlatform = platformService.findPlatformById(id);
        Platform p = optionalPlatform.orElseThrow(PlatformNotFoundException::new);
        return ResponseEntity.ok(getPlatformResource(p));
    }

    /**
     Generates a Platform Response and adds a self link to it.
     */
    private PlatformResponse getPlatformResource(Platform platform) {
        PlatformResponse res = new PlatformResponse(platform);
        res.add(linkTo(methodOn(PlatformController.class).getPlatform(res.getIdentifier())).withSelfRel());
        return res;
    }
}
