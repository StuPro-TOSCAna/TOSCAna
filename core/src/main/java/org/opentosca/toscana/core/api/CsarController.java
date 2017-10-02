package org.opentosca.toscana.core.api;

import org.opentosca.toscana.core.csar.CsarService;
import org.opentosca.toscana.core.transformation.TransformationService;
import org.opentosca.toscana.core.util.PlatformProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * This contoller implements all Csar and Transformation specific operations
 */
@RestController
@RequestMapping("/csars")
public class CsarController {
	
	@Autowired
	public CsarService csarService;
	
	@Autowired
	public TransformationService transformationService;
	
	@Autowired
	public PlatformProvider platformProvider;

	@PutMapping
	@RequestMapping("/{name}/archive")
	public ResponseEntity<String> uploadCSAR(
		@PathVariable(name = "name") String name,
		HttpServletRequest request
	) {
		
		return ResponseEntity.ok().build();
	}
}
