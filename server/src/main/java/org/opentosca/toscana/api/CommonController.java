package org.opentosca.toscana.api;

import javax.servlet.http.HttpServletRequest;

import org.opentosca.toscana.api.docs.HiddenResourceSupport;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 This REST Controller handles the requests which do not relate to platforms or csars.
 <p>
 Currently the only request handled by this controller is the {@code status} request
 <p>
 For sample Responses of the Requests, please have a look at docs/api/api_samples.md
 */
@CrossOrigin
@RestController
@RequestMapping("/api")
@Api(
    value = "/",
    tags = "hypermedia",
    description = "Hypermedia only endpoints"
)
public class CommonController {

    @Value("${endpoints.mappings.enabled}")
    private Boolean actuatorMappingsActive = false;

    @Value("${endpoints.health.path}")
    private String actuatorHealthPath = "";
    @Value("${endpoints.metrics.path}")
    private String actuatorMetricsPath = "";
    @Value("${endpoints.mappings.path}")
    private String actuatorMappingsPath = "";

    @RequestMapping(
        value = {"/status/", "/status"},
        method = RequestMethod.GET,
        produces = "application/hal+json"
    )
    @ApiOperation(
        value = "Hypermedia endpoint to get all supported Status operations",
        notes = "Status operations are based on Spring Boot Actuator",
        produces = "application/hal+json",
        response = ResourceSupport.class
    )
    public ResourceSupport getStatusIndex(HttpServletRequest request) throws Exception {
        String baseURL = request.getRequestURL().toString()
            .replace("/api/status", "").replace("/api/status/", "");
        ResourceSupport support = new HiddenResourceSupport();
        support.add(new Link(baseURL + actuatorHealthPath, "health"));
        support.add(new Link(baseURL + actuatorMetricsPath, "metrics"));
        if (actuatorMappingsActive)
            support.add(new Link(baseURL + actuatorMappingsPath, "mappings"));
        support.add(linkTo(methodOn(CommonController.class).getStatusIndex(null)).withSelfRel());
        return support;
    }

    @RequestMapping(
        value = {"/", ""},
        method = RequestMethod.GET,
        produces = "application/hal+json"
    )
    @ApiOperation(
        value = "Hypermedia endpoint to get all resource endpoints",
        notes = "The retuned links to the endpoints are: Csars, platforms and status",
        produces = "application/hal+json",
        response = ResourceSupport.class
    )
    public ResourceSupport getIndex() throws Exception {
        ResourceSupport support = new HiddenResourceSupport();
        support.add(linkTo(methodOn(CommonController.class).getIndex()).withSelfRel());
        support.add(linkTo(methodOn(CommonController.class).getStatusIndex(null)).withRel("status"));
        support.add(linkTo(methodOn(PlatformController.class).getPlatforms()).withRel("platforms"));
        support.add(linkTo(methodOn(CsarController.class).listCSARs()).withRel("csars"));
        return support;
    }
}
