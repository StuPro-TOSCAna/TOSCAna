package org.opentosca.toscana.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.opentosca.toscana.api.docs.CsarResources;
import org.opentosca.toscana.api.docs.HiddenResources;
import org.opentosca.toscana.api.docs.RestErrorResponse;
import org.opentosca.toscana.api.exceptions.ActiveTransformationsException;
import org.opentosca.toscana.api.exceptions.CsarNotFoundException;
import org.opentosca.toscana.api.model.CsarResponse;
import org.opentosca.toscana.api.model.CsarUploadErrorResponse;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarService;
import org.opentosca.toscana.core.parse.InvalidCsarException;
import org.opentosca.toscana.core.transformation.TransformationState;

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
@SuppressWarnings("DefaultAnnotationParam") //Suppressing this to allow better readability of the Swagger docs in code
@CrossOrigin
@RestController
@RequestMapping("/api/csars")
@Api(
    tags = {"csars"},
    value = "/csars",
    description = "Operations for Cloud Service Archives (CSARs)"
)
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
    @ApiOperation(
        value = "List all CSARs stored on the server",
        notes = "Returns a Hypermedia Resource containing all CSARs" +
            " that have been uploaded to the server and did not get removed",
        response = CsarResources.class,
        produces = "application/hal+json"
    )
    @RequestMapping(
        path = "",
        method = RequestMethod.GET,
        produces = "application/hal+json"
    )
    public ResponseEntity<Resources<CsarResponse>> listCSARs() {
        Link selfLink = linkTo(methodOn(CsarController.class).listCSARs()).withSelfRel();
        List<CsarResponse> responses = new ArrayList<>();
        for (Csar csar : csarService.getCsars()) {
            responses.add(new CsarResponse(csar.getIdentifier(), csar.getLifecyclePhases()));
        }
        Resources<CsarResponse> csarResources = new HiddenResources<>(responses, selfLink);
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
    @ApiOperation(
        value = "Returns details for a specific name (identifier)",
        notes = "Returns the element with the given name, Object contents are " +
            "equal to a regular /csars request (if you just look at the desired entry)"
    )
    @ApiResponses({
        @ApiResponse(
            code = 200,
            message = "The Csar was found and the contents are found in the body",
            response = CsarResponse.class
        ),
        @ApiResponse(
            code = 404,
            message = "There is no CSAR for the given name (identifier)",
            response = RestErrorResponse.class
        )
    })
    @RequestMapping(
        path = "/{name}",
        method = RequestMethod.GET,
        produces = "application/hal+json"
    )
    public ResponseEntity<CsarResponse> getCSARInfo(
        @ApiParam(value = "The unique identifier for the CSAR", required = true, example = "test")
        @PathVariable(name = "name") String name
    ) {
        Csar csar = getCsarForName(name);
        return ResponseEntity.ok().body(new CsarResponse(csar.getIdentifier(), csar.getLifecyclePhases()));
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
    @ApiOperation(
        value = "Creates a new CSAR",
        notes = "This operation creates a new CSAR if the name (identifier) is not used already. " +
            "The uploaded file has to be a valid CSAR archive. " +
            "Once the file was uploaded the server will synchronously " +
            "(the client has to wait for the response) unzip the archive and parse it. " +
            "Upload gets performed using Multipart Form upload.",
        code = 201
    )
    @ApiResponses({
        @ApiResponse(
            code = 201,
            message = "The upload and parsing of the csar was sucessful",
            response = Void.class
        ),
        @ApiResponse(
            code = 400,
            message = "Processing of the csar failed. Information why can be found in the attached error message!",
            response = CsarUploadErrorResponse.class
        ),
        @ApiResponse(
            code = 500,
            message = "The server encountered a unexcpected problem.",
            response = Void.class
        )
    })
    @RequestMapping(
        path = "/{name}",
        method = {RequestMethod.PUT, RequestMethod.POST},
        produces = "application/hal+json"
    )
    public ResponseEntity<Void> uploadCSAR(
        @ApiParam(value = "The unique identifier for the CSAR", required = true)
        @PathVariable(name = "name") String name,
        @ApiParam(value = "The CSAR Archive (Compressed as ZIP)", required = true)
        @RequestParam(name = "file", required = true) MultipartFile file
//        HttpServletRequest request
    ) throws InvalidCsarException {
        try {
            csarService.submitCsar(name, file.getInputStream());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (InvalidCsarException | RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to process submitted CSAR", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @ApiOperation(
        value = "Deletes a Existing CSAR",
        notes = "Deletes the Resulting CSAR and its transformations (if none of them is running). " +
            "If a transformation is running (in the state TRANSFORMING) the csar cannot be deleted!"
    )
    @ApiResponses({
        @ApiResponse(
            code = 200,
            message = "The deletion of the csar was sucessful!",
            response = Void.class
        ),
        @ApiResponse(
            code = 400,
            message = "The deletion of the csar failed, because there is one or more transformations still running.",
            response = RestErrorResponse.class
        ),
        @ApiResponse(
            code = 404,
            message = "There is no CSAR for the given name (identifier)",
            response = RestErrorResponse.class
        )
    })
    @RequestMapping(
        path = "/{name}/delete",
        method = {RequestMethod.DELETE},
        produces = "application/hal+json"
    )
    public ResponseEntity<Void> deleteCsar(
        @ApiParam(value = "The unique identifier for the CSAR", required = true, example = "test")
        @PathVariable("name") String name
    ) {
        Csar csar = getCsarForName(name);

        csar.getTransformations().forEach((k, v) -> {
            if (v.getState() == TransformationState.TRANSFORMING) {
                throw new ActiveTransformationsException(
                    String.format(
                        "Transformation %s/%s is still running. Cannot delete csar while a transformation is running!",
                        name, k
                    )
                );
            }
        });

        csarService.deleteCsar(csar);

        return ResponseEntity.ok().build();
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

    private Csar getCsarForName(@PathVariable("name") String name) {
        Optional<Csar> optionalCsar = csarService.getCsar(name);
        return optionalCsar.orElseThrow(CsarNotFoundException::new);
    }
}
