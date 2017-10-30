package org.opentosca.toscana.core.api;

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
public class CommonController {

    @RequestMapping(value = {"/", ""}, method = RequestMethod.GET)
    public ResourceSupport getIndex() {
        ResourceSupport support = new ResourceSupport();
        support.add(linkTo(methodOn(CommonController.class).getIndex()).withSelfRel());
//        support.add(linkTo(methodOn(CommonController.class).getStatus()).withRel("status"));
        support.add(linkTo(methodOn(PlatformController.class).getPlatforms()).withRel("platforms"));
        support.add(linkTo(methodOn(CsarController.class).listCSARs()).withRel("csars"));
        //TODO add spring boot actuator refs.
        return support;
    }
}
