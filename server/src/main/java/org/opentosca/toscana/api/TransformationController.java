package org.opentosca.toscana.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.opentosca.toscana.api.docs.HiddenResources;
import org.opentosca.toscana.api.docs.RestErrorResponse;
import org.opentosca.toscana.api.docs.TransformationResources;
import org.opentosca.toscana.api.exceptions.CsarNotFoundException;
import org.opentosca.toscana.api.exceptions.IllegalTransformationStateException;
import org.opentosca.toscana.api.exceptions.PlatformNotFoundException;
import org.opentosca.toscana.api.exceptions.TransformationAlreadyPresentException;
import org.opentosca.toscana.api.exceptions.TransformationNotFoundException;
import org.opentosca.toscana.api.model.GetInputsResponse;
import org.opentosca.toscana.api.model.GetOutputsResponse;
import org.opentosca.toscana.api.model.InputWrap;
import org.opentosca.toscana.api.model.InputsResponse;
import org.opentosca.toscana.api.model.LogResponse;
import org.opentosca.toscana.api.model.OutputWrap;
import org.opentosca.toscana.api.model.OutputsResponse;
import org.opentosca.toscana.api.model.TransformationResponse;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarService;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.TransformationService;
import org.opentosca.toscana.core.transformation.TransformationState;
import org.opentosca.toscana.core.transformation.artifacts.TargetArtifact;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.logging.LogEntry;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.platform.PlatformService;
import org.opentosca.toscana.core.transformation.properties.InputProperty;
import org.opentosca.toscana.core.transformation.properties.NoSuchPropertyException;
import org.opentosca.toscana.core.transformation.properties.OutputProperty;
import org.opentosca.toscana.core.transformation.properties.PropertyInstance;
import org.opentosca.toscana.core.transformation.properties.PropertyType;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.compress.utils.IOUtils;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static java.lang.String.format;
import static org.opentosca.toscana.core.transformation.TransformationState.INPUT_REQUIRED;
import static org.opentosca.toscana.core.transformation.TransformationState.READY;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 This controller implements API operations regarding Transformations.
 <p>
 For sample Responses of the Requests, please have a look at docs/api/api_samples.md
 */
@CrossOrigin
@RestController
@RequestMapping("/api/csars/{csarId}/transformations")
@Api(
    value = "/csars/{csarId}/transformations",
    tags = {"transformations"},
    description = "Operations regarding Transformations"
)
public class TransformationController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CsarService csarService;
    private final TransformationService transformationService;
    private final PlatformService platformService;

    @Autowired
    public TransformationController(CsarService csarService,
                                    TransformationService transformationService,
                                    PlatformService platformService) {
        this.csarService = csarService;
        this.transformationService = transformationService;
        this.platformService = platformService;
    }

    /**
     This Request Returns a list of all transformations belonging to a csar.
     <p>
     Accessed with http call <code>GET /csars/{csar}/transformations/</code>
     <table summary="">
     <tr>
     <td>HTTP-Code</td>
     <td>Mime-Type</td>
     <td>Description (Returned if)</td>
     </tr>
     <tr>
     <td>200</td>
     <td>application/hal+json</td>
     <td>Returns the resource (list) of all transformations that belong to this given csar</td>
     </tr>
     <tr>
     <td>404</td>
     <td>application/json</td>
     <td>Returns a error message if the csar is not found (see returned error message for details)</td>
     </tr>
     </table>
     */
    @RequestMapping(
        path = "",
        method = RequestMethod.GET,
        produces = "application/hal+json"
    )
    @ApiOperation(
        value = "List all transformations of a CSAR",
        tags = {"transformations", "csars"},
        notes = "Returns a HAL-Resources list containing all Transformations for a specific CSAR"
    )
    @ApiResponses({
        @ApiResponse(
            code = 200,
            message = "The operation was executed successfully",
            response = TransformationResources.class
        ),
        @ApiResponse(
            code = 404,
            message = "There is no CSAR for the given identifier",
            response = RestErrorResponse.class
        )
    })
    public ResponseEntity<Resources<TransformationResponse>> getCSARTransformations(
        @ApiParam(value = "The unique identifier for the CSAR", required = true, example = "test")
        @PathVariable(name = "csarId") String name
    ) {
        Csar csar = findByCsarId(name);
        Link selfLink =
            linkTo(methodOn(TransformationController.class)
                .getCSARTransformations(name))
                .withSelfRel()
                .expand(name);
        List<TransformationResponse> transformations = new ArrayList<>();
        for (Map.Entry<String, Transformation> entry : csar.getTransformations().entrySet()) {
            transformations.add(new TransformationResponse(
                entry.getValue().getLifecyclePhases(),
                entry.getValue().getState(),
                entry.getKey(),
                csar.getIdentifier()
            ));
        }
        Resources<TransformationResponse> resources = new HiddenResources<>(transformations, selfLink);
        return ResponseEntity.ok(resources);
    }

    /**
     Returns information about a single transformation
     <p>
     Accessed with http call <code>GET /csars/{csar}/transformations/{platform}/</code>
     <table summary="">
     <tr>
     <td>HTTP-Code</td>
     <td>Mime-Type</td>
     <td>Description (Returned if)</td>
     </tr>
     <tr>
     <td>200</td>
     <td>application/hal+json</td>
     <td>Returns a empty body if the transformation has been started</td>
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
        path = "/{platform}",
        method = RequestMethod.GET,
        produces = "application/hal+json"
    )
    @ApiOperation(
        value = "Get details for a specific transformation",
        tags = {"transformations"},
        notes = "Returns a HAL-Resource Containing the details for the transformation with the given parameters"
    )
    @ApiResponses({
        @ApiResponse(
            code = 200,
            message = "The operation was executed successfully"
        ),
        @ApiResponse(
            code = 404,
            message = "There is no CSAR for the given identifier or the CSAR does not have " +
                "a Transformation for the specified platform.",
            response = RestErrorResponse.class
        )
    })
    public ResponseEntity<TransformationResponse> getCSARTransformation(
        @ApiParam(value = "The unique identifier for the CSAR", required = true, example = "test")
        @PathVariable(name = "csarId") String name,
        @ApiParam(value = "The identifier for the platform", required = true, example = "kubernetes")
        @PathVariable(name = "platform") String platform
    ) {
        Csar csar = findByCsarId(name);
        Transformation transformation = findTransformationByPlatform(csar, platform);
        return ResponseEntity.ok().body(new TransformationResponse(
            transformation.getLifecyclePhases(),
            transformation.getState(),
            platform, name
        ));
    }

    /**
     Creates a new transformation for the given platform and csar <p>
     <p>
     Accessed with http call <code>PUT or POST /csars/{csar}/transformations/{platform}/create</code>
     <table summary="">
     <tr>
     <td>HTTP-Code</td>
     <td>Mime-Type</td>
     <td>Description (Returned if)</td>
     </tr>
     <tr>
     <td>200</td>
     <td>application/hal+json</td>
     <td>Returns a empty body if the transformation has been created</td>
     </tr>
     <tr>
     <td>404</td>
     <td>application/json</td>
     <td>Returns a error message if the csar is not found or if the given platform name is not found (see returned error
     message for details)</td>
     </tr>
     </table>
     */
    @RequestMapping(
        path = "/{platform}/create",
        method = {RequestMethod.POST, RequestMethod.PUT},
        produces = "application/hal+json"
    )
    @ApiOperation(
        value = "Create a new Transformation",
        tags = {"transformations"},
        notes = "Creates a new transformation for the given CSAR and Platform " +
            "(If the platform does not exist and there is no other transformation with the same CSAR and Platform, " +
            "you have to delete the old transformation in this case)"
    )
    @ApiResponses({
        @ApiResponse(
            code = 200,
            message = "The operation was executed successfully"
        ),
        @ApiResponse(
            code = 400,
            message = "The transfomation could not get created because there already is a Transformation" +
                " of this CSAR on the given Platform",
            response = RestErrorResponse.class
        ),
        @ApiResponse(
            code = 404,
            message = "There is no CSAR for the given identifier or the platform is not known.",
            response = RestErrorResponse.class
        )
    })
    public ResponseEntity<Void> addTransformation(
        @ApiParam(value = "The unique identifier for the CSAR", required = true, example = "test")
        @PathVariable(name = "csarId") String name,
        @ApiParam(value = "The identifier for the platform", required = true, example = "kubernetes")
        @PathVariable(name = "platform") String platform
    ) {
        logger.info("Creating transformation for csar '{}' on '{}'", name, platform);
        Csar csar = findByCsarId(name);
        //Return bad Request if a transformation for this platform is already present
        if (csar.getTransformation(platform).isPresent()) {
            throw new TransformationAlreadyPresentException();
        }
        //Return 404 if the platform does not exist
        Optional<Platform> optionalPlatform = platformService.findPlatformById(platform);
        Platform p = optionalPlatform.orElseThrow(PlatformNotFoundException::new);
        transformationService.createTransformation(csar, p);
        return ResponseEntity.ok().build();
    }

    /**
     This Mapping starts a transformation
     <p>
     Accessed with http call <code>GET /csars/{csar}/transformations/{platform}/start</code>
     <table summary="">
     <tr>
     <td>HTTP-Code</td>
     <td>Mime-Type</td>
     <td>Description (Returned if)</td>
     </tr>
     <tr>
     <td>200</td>
     <td>application/hal+json</td>
     <td>Returns a empty body if the transformation has been started</td>
     </tr>
     <tr>
     <td>400</td>
     <td>application/json</td>
     <td>Returned if the transformation is not in a valid state (Required Properties missing, Already running/Done) to
     set inputs</td>
     </tr>
     <tr>
     <td>404</td>
     <td>application/json</td>
     <td>Returns a error message if the csar is not found or if the csar does not have a transformation for the given
     name
     (see returned error message for details)</td>
     </tr>
     </table>
     */
    @RequestMapping(
        path = "/{platform}/start",
        method = RequestMethod.POST,
        produces = "application/hal+json"
    )
    @ApiOperation(
        value = "Start a Transformation",
        tags = {"transformations"},
        notes = "Starts a transformation that has been created and is ready to get started. To start a transformation, the " +
            "Transformation has to be in the state READY otherwise the transformation cannot start."
    )
    @ApiResponses({
        @ApiResponse(
            code = 200,
            message = "The operation was executed successfully"
        ),
        @ApiResponse(
            code = 400,
            message = "The state of the transformation is illegal. This means that the transformation is not in the" +
                "READY state. Therefore starting it is not possible",
            response = RestErrorResponse.class
        ),
        @ApiResponse(
            code = 404,
            message = "There is no CSAR for the given identifier or the CSAR does not have " +
                "a Transformation for the specified platform.",
            response = RestErrorResponse.class
        )
    })
    public ResponseEntity startTransformation(
        @ApiParam(value = "The unique identifier for the CSAR", required = true, example = "test")
        @PathVariable(name = "csarId") String name,
        @ApiParam(value = "The identifier for the platform", required = true, example = "kubernetes")
        @PathVariable(name = "platform") String platform
    ) {
        logger.info("Starting transformation for csar '{}' on '{}'", name, platform);
        Csar csar = findByCsarId(name);
        Transformation transformation = findTransformationByPlatform(csar, platform);
        if (transformationService.startTransformation(transformation)) {
            return ResponseEntity.ok().build();
        } else {
            throw new IllegalTransformationStateException("Transformation could not start because" +
                " its not in a valid state to start.");
        }
    }

    /**
     Deletes a transformation. If it is still running, this request is ignored (returning 400)
     <p>
     Accessed with http call <code>DELETE /csars/{csar}/transformations/{platform}/delete</code>
     <p>
     <table summary="">
     <tr>
     <td>HTTP-Code</td>
     <td>Mime-Type</td>
     <td>Description (Returned if)</td>
     </tr>
     <tr>
     <td>200</td>
     <td>application/hal+json</td>
     <td>Returns a Empty body if the deletion was successful</td>
     </tr>
     <tr>
     <td>400</td>
     <td>application/hal+json</td>
     <td>Returns a empty body if the transformation cannot be deleted (if its still running)</td>
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
        path = "/{platform}/delete",
        method = RequestMethod.DELETE,
        produces = "application/hal+json"
    )
    @ApiOperation(
        value = "Delete a transformation",
        tags = {"transformations"},
        notes = "Deletes a transformation and all the coresponding artifacts"
    )
    @ApiResponses({
        @ApiResponse(
            code = 200,
            message = "The operation was executed successfully"
        ),
        @ApiResponse(
            code = 400,
            message = "The Deletion of the csar failed",
            response = Void.class
        ),
        @ApiResponse(
            code = 404,
            message = "There is no CSAR for the given identifier or the CSAR does not have " +
                "a Transformation for the specified platform.",
            response = RestErrorResponse.class
        )
    })
    public ResponseEntity<Void> deleteTransformation(
        @ApiParam(value = "The unique identifier for the CSAR", required = true, example = "test")
        @PathVariable(name = "csarId") String name,
        @ApiParam(value = "The identifier for the platform", required = true, example = "kubernetes")
        @PathVariable(name = "platform") String platform
    ) {
        Csar csar = findByCsarId(name);
        Transformation transformation = findTransformationByPlatform(csar, platform);

        if (transformationService.deleteTransformation(transformation)) {
            // return 200 if the deletion was successful
            return ResponseEntity.ok().build();
        } else {
            // return 400 if the deletion failed
            return ResponseEntity.status(400).build();
        }
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
        path = "/{platform}/logs",
        method = RequestMethod.GET,
        produces = "application/hal+json"
    )
    @ApiOperation(
        value = "Get the logs for a Transformation",
        tags = {"transformations"},
        notes = "Returns the logs for a transformation, starting at a specific position. from the given start index all " +
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
            message = "There is no CSAR for the given identifier or the CSAR does not have " +
                "a Transformation for the specified platform.",
            response = RestErrorResponse.class
        )
    })
    public ResponseEntity<LogResponse> getTransformationLogs(
        @ApiParam(value = "The unique identifier for the CSAR", required = true, example = "test")
        @PathVariable(name = "csarId") String csarId,
        @ApiParam(value = "The identifier for the platform", required = true, example = "kubernetes")
        @PathVariable(name = "platform") String platformId,
        @ApiParam(value = "The index of the first log entry you want (0 returns the whole log)", required = true, example = "0")
        @RequestParam(name = "start", required = false, defaultValue = "0") Long start
    ) {
        if (start < 0) {
            throw new IndexOutOfBoundsException("the start index has to be at least 0");
        }
        Csar csar = findByCsarId(csarId);
        Transformation transformation = findTransformationByPlatform(csar, platformId);
        Log log = transformation.getLog();
        List<LogEntry> entries = log.getLogEntries(Math.toIntExact(start));
        return ResponseEntity.ok().body(new LogResponse(
            start,
            entries.size() == 0 ? start : start + entries.size() - 1, //TODO Maybe move this calculation into the LogResponse Class
            entries,
            platformId,
            csarId
        ));
    }

    /**
     Returns the URL to download the target Artifact (Wrapped in json)<p>
     <p>
     Accessed with http call <code>GET /csars/{csar}/transformations/{platform}/artifacts</code>
     <table summary="">
     <tr>
     <td>HTTP-Code</td>
     <td>Mime-Type</td>
     <td>Description (Returned if)</td>
     </tr>
     <tr>
     <td>200</td>
     <td>application/hal+json</td>
     <td>Returns a Json object containing the download url for the artifact</td>
     </tr>
     <tr>
     <td>400</td>
     <td>application/json</td>
     <td>Returned if the transformation is not in a valid state (has to be in DONE or ERROR) to set inputs</td>
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
        path = "/{platform}/artifact",
        method = RequestMethod.GET,
        produces = "application/octet-stream"
    )
    @ApiOperation(
        value = "Download the target artifact archive",
        tags = {"transformations"},
        notes = "Once the transformation is done (in the state DONE) or it has encountered a error (state ERROR). " +
            "It is possible to download a archive (ZIP format) of all the files generated while the transformation was " +
            "running."
    )
    @ApiResponses({
        @ApiResponse(
            code = 200,
            message = "The operation was executed successfully"
        ),
        @ApiResponse(
            code = 400,
            message = "There is nothing to download yet because the execution of the transformation has not yet started " +
                "or is not finished (With or without errors)",
            response = RestErrorResponse.class
        ),
        @ApiResponse(
            code = 404,
            message = "There is no CSAR for the given identifier or the CSAR does not have " +
                "a Transformation for the specified platform.",
            response = RestErrorResponse.class
        )
    })
    public ResponseEntity<Void> getTransformationArtifact(
        @ApiParam(value = "The unique identifier for the CSAR", required = true, example = "test")
        @PathVariable(name = "csarId") String csarName,
        @ApiParam(value = "The identifier for the platform", required = true, example = "kubernetes")
        @PathVariable(name = "platform") String platform,
        HttpServletResponse response
    ) throws IOException {
        Csar csar = csarService.getCsar(csarName).orElseThrow(CsarNotFoundException::new);
        Transformation transformation = csar.getTransformation(platform).orElseThrow(TransformationNotFoundException::new);
        TargetArtifact artifact = transformation.getTargetArtifact().orElseThrow(() ->
            new IllegalTransformationStateException(
                format("Artifact for csar '{}' and platform '{}' not found", csarName, platform)
            )
        );

        response.setHeader("Content-Disposition", "attachment; filename=\"" + artifact.name + "\"");
        response.setHeader("Content-Type", "application/octet-stream");
        response.setHeader("Content-Length", artifact.getFileSize() + "");

        InputStream in = artifact.readAccess();
        OutputStream out = response.getOutputStream();
        IOUtils.copy(in, out);
        in.close();
        out.close();
        return ResponseEntity.ok().build();
    }

    /**
     Returns a list of inputs that might have to be set (if they are required) in order to start
     the transformation <p>
     Accessed with http call <code>GET /csars/{csar}/transformations/{platform}/inputs</code>
     <table summary="">
     <tr>
     <td>HTTP-Code</td>
     <td>Mime-Type</td>
     <td>Description (Returned if)</td>
     </tr>
     <tr>
     <td>200</td>
     <td>application/hal+json</td>
     <td>Returned with content body (See samples.md) if the operation was successful</td>
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
        path = "/{platform}/inputs",
        method = RequestMethod.GET,
        produces = "application/hal+json"
    )
    @ApiOperation(
        value = "Retrieve the inputs of this transformation",
        tags = {"transformations"},
        notes = "This Operation returns a list of inputs, specific to the csar and the platform. " +
            "If the input is invalid it has to be set in order to proceed with " +
            "starting the transformation. Setting the inputs is done with a POST or PUT to the same URL " +
            "(See Set Inputs Operation). If Transformation does not have any inputs, an empty array " +
            "is returned"
    )
    @ApiResponses({
        @ApiResponse(
            code = 200,
            message = "The operation was executed successfully",
            response = GetInputsResponse.class
        ),
        @ApiResponse(
            code = 404,
            message = "There is no CSAR for the given identifier or the CSAR does not have " +
                "a Transformation for the specified platform.",
            response = RestErrorResponse.class
        )
    })
    public ResponseEntity<GetInputsResponse> getInputs(
        @ApiParam(value = "The identifier for the CSAR", required = true, example = "test")
        @PathVariable(name = "csarId") String csarId,
        @ApiParam(value = "The identifier for the platform", required = true, example = "kubernetes")
        @PathVariable(name = "platform") String platformId
    ) {
        Csar csar = findByCsarId(csarId);
        Transformation transformation = findTransformationByPlatform(csar, platformId);
        List<InputWrap> inputWrapList = toPropertyWrapList(transformation.getInputs(), null);
        GetInputsResponse response = new GetInputsResponse(csarId, platformId, inputWrapList);
        return ResponseEntity.ok(response);
    }

    /**
     This mapping is used to post the inputs to the server.
     <p>
     Accessed with http call <code>PUT or POST /csars/{csar}/transformations/{platform}/inputs</code>
     <table summary="">
     <tr>
     <td>HTTP-Code</td>
     <td>Mime-Type</td>
     <td>Description (Returned if)</td>
     </tr>
     <tr>
     <td>200</td>
     <td>application/hal+json</td>
     <td>Returns a empty body if all required inputs have been set</td>
     </tr>
     <tr>
     <td>400</td>
     <td>application/json</td>
     <td>Returned if the transformation is not in a valid state (has to be in INPUT_REQUIRED or READY)</td>
     </tr>
     <tr>
     <td>404</td>
     <td>application/json</td>
     <td>Returns an error message if the csar is not found or if the csar does not have a transformation for the given
     platformId (see returned error message for details)</td>
     </tr>
     </table>
     */
    @RequestMapping(
        path = "/{platform}/inputs",
        method = {RequestMethod.POST, RequestMethod.PUT},
        produces = "application/json"
    )
    @ApiOperation(
        value = "Set the value of inputs",
        tags = {"transformations"},
        notes = "With this method it is possible to set the value of an input or multiple inputs at once. The values " +
            "of inputs can be set as long as they are in the READY or INPUT_REQUIRED state. The transformation changes its state " +
            "to ready once all required inputs have a valid value assigned to them."
    )
    @ApiResponses({
        @ApiResponse(
            code = 200,
            message = "The operation was executed successfully",
            response = Void.class
        ),
        @ApiResponse(
            code = 400,
            message = "Inputs cannot get set once the transformation has been started.",
            response = RestErrorResponse.class
        ),
        @ApiResponse(
            code = 404,
            message = "There is no CSAR for the given identifier or the CSAR does not have " +
                "a transformation for the specified platform.",
            response = RestErrorResponse.class
        ),
        @ApiResponse(
            code = 406,
            message = "At least one of the inputs could not get set because either the key does not exist or the " +
                "syntax validation of the value has failed.",
            response = InputsResponse.class
        )
    })
    public ResponseEntity<InputsResponse> setInputs(
        @ApiParam(value = "The unique identifier for the CSAR", required = true, example = "test")
        @PathVariable(name = "csarId") String csarId,
        @ApiParam(value = "The identifier for the platform", required = true, example = "kubernetes")
        @PathVariable(name = "platform") String platformId,
        @RequestBody InputsResponse propertiesRequest
    ) {
        Csar csar = findByCsarId(csarId);
        Transformation transformation = findTransformationByPlatform(csar, platformId);
        List<TransformationState> validStates = Arrays.asList(INPUT_REQUIRED, READY);
        if (!Arrays.asList(INPUT_REQUIRED, READY).contains(transformation.getState())) {
            throw new IllegalTransformationStateException(
                String.format("The transformation is not in one of the states '%s'", validStates));
        }
        PropertyInstance inputs = transformation.getInputs();
        Map<String, Boolean> successes = new HashMap<>();
        boolean somethingFailed = false;
        //Set the properties and check their validity
        for (InputWrap entry : propertiesRequest.getInputs()) {
            try {
                boolean success = inputs.set(entry.getKey(), entry.getValue());
                successes.put(entry.getKey(), success);
                if (!success) {
                    somethingFailed = true;
                }
            } catch (NoSuchPropertyException e) {
                logger.error("Failed to set inputs for transformation '%s'", transformation, e);
                somethingFailed = true;
                successes.put(entry.getKey(), false);
            }
        }
        if (!somethingFailed) {
            return ResponseEntity.ok().build();
        } else {
            Set<String> requestedKeys = new HashSet<>(successes.keySet());
            List<InputWrap> propWrapList = toPropertyWrapList(inputs, requestedKeys);
            //The request contains invalid values
            if (requestedKeys.size() > propWrapList.size()) {
                Set<String> knownKeys = propWrapList.stream().map(InputWrap::getKey).collect(Collectors.toSet());
                requestedKeys.removeAll(knownKeys); // Remove all known (valid) keys
                requestedKeys.forEach(e -> {
                    propWrapList.add(new InputWrap(e, PropertyType.INVALID_KEY, "Invalid Key",
                        null, false, null, false));
                });
            }
            InputsResponse response = new InputsResponse(propWrapList);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
        }
    }

    /**
     Returns the outputs of a Transformation.
     <p>
     <table summary="">
     <tr>
     <td>HTTP-Code</td>
     <td>Mime-Type</td>
     <td>Description (Returned if)</td>
     </tr>
     <tr>
     <td>200</td>
     <td>application/hal+json</td>
     <td>Returns a List of the outputs. (Empty if no deployment has been executed)</td>
     </tr>
     <tr>
     <td>400</td>
     <td>application/json</td>
     <td>Returned if the transformation is not in a valid state (has to be in DONE or ERROR) to set inputs</td>
     </tr>
     <tr>
     <td>404</td>
     <td>application/json</td>
     <td>Returns a error message if the csar is not found or if the csar does not have a transformation for the given
     name (see returned error message for details)</td>
     </tr>
     </table>
     */
    @ApiOperation(
        value = "Retrieve the outputs and their values",
        tags = {"transformations"},
        notes = "This operation returns the outputs of a deployment. Retrieval of the outputs is not possible " +
            "if the transformation (including deployment) is not done yet"
    )
    @ApiResponses({
        @ApiResponse(
            code = 200,
            message = "The operation was executed successfully",
            response = GetOutputsResponse.class
        ),
        @ApiResponse(
            code = 404,
            message = "There is no CSAR for the given identifier or the CSAR does not have " +
                "a transformation for the specified platform",
            response = RestErrorResponse.class
        ),
        @ApiResponse(
            code = 400,
            message = "The state of the transformation is invalid (not ERROR or DONE)",
            response = RestErrorResponse.class
        )
    })
    @RequestMapping(
        path = "/{platform}/outputs",
        method = {RequestMethod.GET},
        produces = "application/hal+json"
    )
    public ResponseEntity<GetOutputsResponse> getOutputs(
        @ApiParam(value = "The unique identifier for the CSAR", required = true, example = "test")
        @PathVariable(name = "csarId") String csarId,
        @ApiParam(value = "The identifier for the platform", required = true, example = "kubernetes")
        @PathVariable(name = "platform") String platformId
    ) {
        Csar csar = findByCsarId(csarId);
        Transformation transformation = findTransformationByPlatform(csar, platformId);
        if (transformation.getState() != TransformationState.DONE && transformation.getState() != TransformationState.ERROR) {
            throw new IllegalTransformationStateException("The Transformation has not finished yet!");
        }
        List<OutputProperty> outputs = transformation.getOutputs();
        List<OutputWrap> wrappedOutputs = outputs.stream()
            .map(OutputWrap::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(new GetOutputsResponse(csarId, platformId, wrappedOutputs));
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Cannot delete csar with running transformations")
    @ExceptionHandler(IndexOutOfBoundsException.class)
    public void handleLogIndexLessThanZero() {
        // noop
    }

    /**
     Uses the csar service to find the csar instance (and handles error management)
     */
    private Csar findByCsarId(String csarId) {
        Optional<Csar> csar = csarService.getCsar(csarId);
        return csar.orElseThrow(() -> new CsarNotFoundException(format("No csar with name '%s' found", csarId)));
    }

    /**
     Uses the csar to find the transformation instance (and handles error management)
     */
    private Transformation findTransformationByPlatform(Csar csar, String platformId) {
        Optional<Transformation> transformation = csar.getTransformation(platformId);
        return transformation.orElseThrow(() -> new TransformationNotFoundException(
            format("The Csar '%s' does not have a transformation for platform '%s'", csar.getIdentifier(), platformId)));
    }

    private List<InputWrap> toPropertyWrapList(PropertyInstance instance, Set<String> validKeys) {
        List<InputWrap> inputWrapList = new ArrayList<>();
        for (InputProperty property : instance.getProperties().values()) {
            if (validKeys == null || validKeys.contains(property.getKey())) {
                inputWrapList.add(new InputWrap(property));
            }
        }
        return inputWrapList;
    }
}
