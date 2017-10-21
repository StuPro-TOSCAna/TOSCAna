package org.opentosca.toscana.core.api;

import org.apache.tomcat.util.http.fileupload.FileItemIterator;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.opentosca.toscana.core.api.exceptions.CsarNameAlreadyUsedException;
import org.opentosca.toscana.core.api.exceptions.CsarNotFoundException;
import org.opentosca.toscana.core.api.model.CsarResponse;
import org.opentosca.toscana.core.api.model.CsarUploadErrorResponse;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarService;
import org.opentosca.toscana.core.parse.InvalidCsarException;
import org.opentosca.toscana.core.transformation.platform.PlatformService;
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
 * <p>
 * For sample Responses of the Requests, please have a look at docs/api/api_samples.md
 */
@CrossOrigin
@RestController
@RequestMapping("/csars")
public class CsarController {

    private final CsarService csarService;
    private final PlatformService platformService;

    public Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    public CsarController(CsarService csarService, PlatformService platformService) {
        this.csarService = csarService;
        this.platformService = platformService;
    }

    /**
     * Responds with a list of csars stored on the transformator.
     * <p>
     * This always responds with HTTP-Code 200 (application/hal+json)
     */
    @RequestMapping(
        path = "",
        method = RequestMethod.GET,
        produces = "application/hal+json"
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
     * if not Http 404 (application/hal+json) with a standart error message is returned (see samples.md for a example)
     */
    @RequestMapping(
        path = "/{name}",
        method = RequestMethod.GET,
        produces = "application/hal+json"
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
            throw new CsarNotFoundException();
        }
        return ResponseEntity.ok().body(new CsarResponse(archive.getIdentifier()));
    }

    /**
     * This Request (Supporting Post and Put mehtods) uploads a csar to the transformator.
     * <p>
     * If the upload succeeded HTTP code 200 (no Content) is returned
     * and HTTP code 400 (application/hal+json, with standart error message) if a csar with the given name already exists.
     * <p>
     * Http Code 400 (application/hal+json) with a special repsonse also containing the log output is returned if the parsing of the csar has failed
     * <p>
     * If something goes wrong while processing the upload HTTP Code 500 gets returned (no Content)
     */
    @RequestMapping(
        path = "/{name}",
        method = {RequestMethod.PUT, RequestMethod.POST},
        produces = "application/hal+json"
    )
    public ResponseEntity<String> uploadCSAR(
        @PathVariable(name = "name") String name,
//		@RequestParam(name = "file", required = true) MultipartFile file
        HttpServletRequest request
    ) throws InvalidCsarException {
        try {
//			Csar result = csarService.submitCsar(name, file.getInputStream());
            Csar result = csarService.submitCsar(name, getInputStream(request));
            if (result == null) {
                throw new CsarNameAlreadyUsedException();
            }
            return ResponseEntity.ok().build();
        } catch (InvalidCsarException e) {
            throw e;
        } catch (Exception e) {
            log.error("Reading of uploaded CSAR Failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * This exception handler creates the response for a failed upload (parsing failiure)
     * The response also contains the log messages produced during parsing!
     */
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidCsarException.class)
    public CsarUploadErrorResponse onUploadError(
        HttpServletRequest request,
        InvalidCsarException e
    ) {
        return new CsarUploadErrorResponse(e, request.getServletPath(), 400);
    }

    /**
     * Internal helper method to allow the upload of files!
     */
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
