package org.opentosca.toscana.core.api;

import org.opentosca.toscana.core.api.model.CsarResponse;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarService;
import org.opentosca.toscana.core.transformation.platform.PlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * This contoller implements all Csar and Transformation specific operations
 * <p>
 * For sample Responses of the Requests, please have a look at docs/api/api_samples.md
 */
@CrossOrigin
@RestController
@RequestMapping("/csars")
public class CsarController {

	@Autowired
	public CsarService csarService;

	@Autowired
	public PlatformService platformService;

	public Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Responds with a list of csars stored on the transformator.
	 * <p>
	 * This always responds with HTTP-Code 200 (application/hal+json)
	 */
	@RequestMapping(
		path = "",
		method = RequestMethod.GET,
		produces = "application/json"
	)
	public ResponseEntity<Resources<CsarResponse>> listCSARs() {
		Link selfLink = linkTo(methodOn(CsarController.class).listCSARs()).withSelfRel();
		List<CsarResponse> responses = new ArrayList<>();
		for (Csar csar : csarService.getCsars()) {
			responses.add(new CsarResponse(csar.getIdentifier()));
		}
		Resources<CsarResponse> csarResources = new Resources<>(responses, selfLink);
		return ResponseEntity.ok().body(csarResources);
	}

	/**
	 * Responds with the data for a specific CSAR
	 * <p>
	 * This Operation Response returns 200 (application/hal+json) if a archive with the given name exists
	 * if not Http 404 (no content) with no content is returned
	 */
	@RequestMapping(
		path = "/{name}",
		method = RequestMethod.GET,
		produces = "application/json"
	)
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

	/**
	 * This Request (Supporting Post and Put mehtods) uploads a csar to the transformator.
	 * <p>
	 * If the upload succeeded HTTP code 200 (no Content) is returned
	 * and HTTP code 400 (no Content) if a csar with the given name already exists.
	 * <p>
	 * If something goes wrong while processing the upload HTTP Code 500 gets returned (no Content)
	 */
	@RequestMapping(
		path = "/{name}",
		method = {RequestMethod.PUT, RequestMethod.POST},
		produces = "application/json"
	)
	public ResponseEntity<String> uploadCSAR(
		@PathVariable(name = "name") String name,
		@RequestParam(name = "file", required = true) MultipartFile file
	) {
		try {
			Csar result = csarService.submitCsar(name, file.getInputStream());
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

	//NOTE: This operation might be used again, depending on how spring behaves when uploading large files
//	private InputStream getInputStream(HttpServletRequest request) throws IOException, FileUploadException {
//		ServletFileUpload upload = new ServletFileUpload();
//		FileItemIterator iterator = upload.getItemIterator(request);
//		while (iterator.hasNext()) {
//			FileItemStream stream = iterator.next();
//			if (Objects.equals(stream.getFieldName(), "file")) {
//				return stream.openStream();
//			}
//		}
//		return null;
//	}
}
