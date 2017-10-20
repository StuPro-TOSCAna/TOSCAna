package org.opentosca.toscana.core.api;

import org.opentosca.toscana.core.api.model.StatusResponse;
import org.opentosca.toscana.core.util.FileSystem;
import org.opentosca.toscana.core.util.status.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;


/**
 * This REST Controller handles the requests which do not relate to Platforms or Csars
 * Currently the only request handled by this controller is the <code>/status</code> request
 * <p>
 * For sample Responses of the Requests, please have a look at docs/api/api_samples.md
 */
@CrossOrigin
@RestController
public class CommonController {

	private final StatusService statusService;

	private final FileSystem fileSystem;

	@Autowired
	public CommonController(StatusService statusService, FileSystem fileSystem) {
		this.statusService = statusService;
		this.fileSystem = fileSystem;
	}
    
	/**
	 * Responds with the status of the Transformator, including Available Disk space and the current status
	 * <p>
	 * This Operation always Reponds with HTTP-Code 200
	 */
	@RequestMapping(
		path = "/status",
		method = RequestMethod.GET,
		produces = "application/hal+json"
	)
	public ResponseEntity<StatusResponse> getStatus() {
		StatusResponse response = new StatusResponse(
			statusService.getSystemStatus().getDisplayName(),
			fileSystem.getAvailableSpace(),
			fileSystem.getUsedSpace() + fileSystem.getAvailableSpace()
		);
		response.add(linkTo(methodOn(CommonController.class).getStatus()).withSelfRel());
		return ResponseEntity.ok(response);
	}

}
