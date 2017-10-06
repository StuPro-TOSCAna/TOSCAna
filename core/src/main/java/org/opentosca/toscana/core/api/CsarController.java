package org.opentosca.toscana.core.api;

import org.apache.tomcat.util.http.fileupload.FileItemIterator;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.opentosca.toscana.core.api.model.CsarResponse;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarService;
import org.opentosca.toscana.core.util.PlatformProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * This contoller implements all Csar and Transformation specific operations
 */
@RestController
@RequestMapping("/csars")
public class CsarController {

	@Autowired
	public CsarService csarService;

	@Autowired
	public PlatformProvider platformProvider;

	public Logger log = LoggerFactory.getLogger(getClass());

	@GetMapping
	public ResponseEntity<Resources<CsarResponse>> listCSARs() {
		Link selfLink = linkTo(methodOn(CsarController.class).listCSARs()).withSelfRel();
		List<CsarResponse> responses = new ArrayList<>();
		for (Csar csar : csarService.getCsars()) {
			responses.add(new CsarResponse(csar.getIdentifier()));
		}
		Resources<CsarResponse> csarResources = new Resources<>(responses, selfLink);
		return ResponseEntity.ok().body(csarResources);
	}

	@GetMapping
	@RequestMapping("/{name}")
	public ResponseEntity<CsarResponse> getCSARInfo(
		@PathVariable(name = "name") String name
	) {
		Csar archive = null;
		for (Csar csar : csarService.getCsars()) {
			if (name.equals(csar.getIdentifier())) {
				archive = csar;
				break;
			}
		}
		if (archive == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok().body(new CsarResponse(archive.getIdentifier()));
	}

	@PutMapping
	@RequestMapping("/{name}/archive")
	public ResponseEntity<String> uploadCSAR(
		@PathVariable(name = "name") String name,
		HttpServletRequest request
	) {
		try {
			Csar result = csarService.submitCsar(name, getInputStream(request));
			if (result == null) {
				//Return Bad Request if a csar with this name already exists
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			log.error("Reading of uploaded CSAR Failed", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	private InputStream getInputStream(HttpServletRequest request) throws IOException, FileUploadException {
		ServletFileUpload upload = new ServletFileUpload();
		FileItemIterator iterator = upload.getItemIterator(request);
		while (iterator.hasNext()) {
			FileItemStream stream = iterator.next();
			if (Objects.equals(stream.getFieldName(), "file")) {
				return stream.openStream();
			}
		}
		return null;
	}
}
