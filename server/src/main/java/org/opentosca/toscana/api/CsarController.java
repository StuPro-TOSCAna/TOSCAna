package org.opentosca.toscana.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.opentosca.toscana.api.docs.CsarResources;
import org.opentosca.toscana.api.docs.HiddenResources;
import org.opentosca.toscana.api.docs.RestErrorResponse;
import org.opentosca.toscana.api.exceptions.ActiveTransformationsException;
import org.opentosca.toscana.api.exceptions.CsarNotFoundException;
import org.opentosca.toscana.api.model.CsarResponse;
import org.opentosca.toscana.api.model.LogResponse;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarIdNotUniqueException;
import org.opentosca.toscana.core.csar.CsarService;
import org.opentosca.toscana.core.transformation.TransformationState;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.logging.LogEntry;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
            message = "The upload of the csar was successful",
            response = Void.class
        ),
        @ApiResponse(
            code = 406,
            message = "CSAR upload rejected - given ID already in use",
            response = Void.class
        ),
        @ApiResponse(
            code = 500,
            message = "The server encountered a unexpected problem",
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
    ) {
        try {
            csarService.submitCsar(name, file.getInputStream());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (CsarIdNotUniqueException e) {
            log.info("Rejecting csar upload: Id '{}' already in use", name);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to process submitted CSAR", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @ApiOperation(
        value = "Deletes a Existing CSAR",
        notes = "Deletes the Resulting CSAR and its transformations (if none of them is running). " +
            "If a transformation is running (in the state TRANSFORMING) the CSAR cannot be deleted"
    )
    @ApiResponses({
        @ApiResponse(
            code = 200,
            message = "The deletion of the CSAR was successful",
            response = Void.class
        ),
        @ApiResponse(
            code = 400,
            message = "The deletion of the CSAR failed, because there is one or more transformations still running.",
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
        @ApiParam(value = "The unique identifier for the CSAR", required = true, example = "my-csar-name")
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

    private Csar getCsarForName(@PathVariable("name") String name) {
        Optional<Csar> optionalCsar = csarService.getCsar(name);
        return optionalCsar.orElseThrow(CsarNotFoundException::new);
    }

    /**
     Returns the logs from a given start index to the current end of the logger file. If the start index is higher then
     the current end index, a empty list is returned! The start parameter is a URL encoded parameter
     (<code>?start=0</code>)
     <p>
     Accessed with http call <code>GET /csars/{csar}/transformations/{platform}/logs</code>
     <table summary="">
     <tr>
     <td>HTTP-Code</td>
     <td>Mime-Type</td>
     <td>Description (Returned if)</td>
     </tr>
     <tr>
     <td>200</td>
     <td>application/hal+json</td>
     <td>Returns a Json object containing the desired part of the logger for the transformation</td>
     </tr>
     <tr>
     <td>404</td>
     <td>application/json</td>
     <td>Returns a error message if the csar is not found or if the csar does not have a transformation for the given
     name (see returned error message for details)</td>
     </tr>
     </table>
     */
    @RequestMapping(
        path = "/{name}/logs",
        method = RequestMethod.GET,
        produces = "application/hal+json"
    )
    @ApiOperation(
        value = "Get the logs of a csar",
        tags = {"csars"},
        notes = "Returns the logs for a csar, starting at a specific position. from the given start index all " +
            "following log lines get returned. If the start index is larger than the current last log index the operation " +
            "will return a empty list."
    )
    @ApiResponses({
        @ApiResponse(
            code = 200,
            message = "The operation was executed successfully",
            response = LogResponse.class
        ),
        @ApiResponse(
            code = 400,
            message = "The given start value is less than zero",
            response = RestErrorResponse.class
        ),
        @ApiResponse(
            code = 404,
            message = "There is no CSAR for the given identifier",
            response = RestErrorResponse.class
        )
    })
    public ResponseEntity<LogResponse> getLogs(
        @ApiParam(value = "The unique identifier for the CSAR", required = true, example = "test")
        @PathVariable(name = "name") String csarId,
        @ApiParam(value = "The index of the first log entry you want (0 returns the whole log)", required = true, example = "0")
        @RequestParam(name = "start", required = false, defaultValue = "0") Long start
    ) {
        if (start < 0) {
            throw new IndexOutOfBoundsException("the start index has to be at least 0");
        }
        Csar csar = getCsarForName(csarId);
        Log log = csar.getLog();
        List<LogEntry> entries = log.getLogEntries(Math.toIntExact(start));
        return ResponseEntity.ok().body(new LogResponse(
            start,
            entries.size() == 0 ? start : start + entries.size() - 1,
            entries,
            csarId
        ));
    }
}
