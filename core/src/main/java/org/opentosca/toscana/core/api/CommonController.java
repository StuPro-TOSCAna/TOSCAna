package org.opentosca.toscana.core.api;

import org.opentosca.toscana.core.api.model.StatusResponse;
import org.opentosca.toscana.core.util.FileSystem;
import org.opentosca.toscana.core.util.StatusProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/")
public class CommonController {
	
	@Autowired
	public StatusProvider statusProvider;
	
	@Autowired
	public FileSystem fileSystem;
	
	@RequestMapping("/status")
	@GetMapping
	public ResponseEntity<StatusResponse> getStatus() {
		StatusResponse response = new StatusResponse(
			statusProvider.getSystemStatus().getDisplayName(),
			fileSystem.getAvailableSpace(),
			fileSystem.getUsedSpace()+fileSystem.getAvailableSpace()
		);
		response.add(linkTo(methodOn(CommonController.class).getStatus()).withSelfRel());
		return ResponseEntity.ok(response);
	}
}
