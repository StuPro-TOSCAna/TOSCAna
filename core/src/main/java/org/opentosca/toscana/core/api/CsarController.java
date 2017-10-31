package org.opentosca.toscana.core.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.opentosca.toscana.core.api.exceptions.CsarNotFoundException;
import org.opentosca.toscana.core.api.model.CsarResponse;
import org.opentosca.toscana.core.api.model.CsarUploadErrorResponse;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarService;
import org.opentosca.toscana.core.parse.InvalidCsarException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 This controller implements all Csar and Transformation specific operations
 <p>
 For sample Responses of the Requests,
 please have a look at docs/api/api_samples.md
 */
@CrossOrigin
@RestController
@RequestMapping("/api/csars")
public class CsarController {

    private final static Logger log = LoggerFactory.getLogger(CsarController.class);

    private final CsarService csarService;

    @Autowired
    public CsarController(CsarService csarService) {
        this.csarService = csarService;
    }

    /**
     Responds with a list of csars stored on the transformer.
     <p>
     This always responds with HTTP-Code 200 (application/hal+json)
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
     Responds with the data for a specific CSAR
     <p>
     <b>HTTP </b>
     <p>
     200 (application/hal+json): if a archive with the given name exists if not
     404 (application/hal+json): with a standard error message is
     returned (see samples.md for a example)
     */
    @RequestMapping(
        path = "/{name}",
        method = RequestMethod.GET,
        produces = "application/hal+json"
    )
    public ResponseEntity<CsarResponse> getCSARInfo(
        @PathVariable(name = "name") String name
    ) {
        Optional<Csar> optionalCsar = csarService.getCsar(name);
        Csar csar = optionalCsar.orElseThrow(CsarNotFoundException::new);
        return ResponseEntity.ok().body(new CsarResponse(csar.getIdentifier()));
    }

    /**
     This Request (Supporting Post and Put methods) uploads a csar to the transformer.
     <p>
     <b>HTTP Response Codes</b>
     <p>
     200 (no Content): Upload succeeded
     400 (application/hal+json): parsing of the csar failed. Response contains logs of parser.
     <p>
     500: Processing failed
     */
    @RequestMapping(
        path = "/{name}",
        method = {RequestMethod.PUT, RequestMethod.POST},
        produces = "application/hal+json"
    )
    public ResponseEntity<String> uploadCSAR(
        @PathVariable(name = "name") String name,
        @RequestParam(name = "file", required = true) MultipartFile file
//        HttpServletRequest request
    ) throws InvalidCsarException {
        try {
            csarService.submitCsar(name, file.getInputStream());
            return ResponseEntity.ok().build();
        } catch (InvalidCsarException | RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to process submitted CSAR", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     This exception handler creates the response for a failed upload (parsing failure).
     <p>
     The response also contains the log messages produced during parsing.
     */
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidCsarException.class)
    public CsarUploadErrorResponse onUploadError(
        HttpServletRequest request,
        InvalidCsarException e
    ) {
        return new CsarUploadErrorResponse(e, request.getServletPath(), 400);
    }
}
