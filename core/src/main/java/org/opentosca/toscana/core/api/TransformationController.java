package org.opentosca.toscana.core.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opentosca.toscana.core.api.exceptions.CsarNotFoundException;
import org.opentosca.toscana.core.api.exceptions.IllegalTransformationStateException;
import org.opentosca.toscana.core.api.exceptions.PlatformNotFoundException;
import org.opentosca.toscana.core.api.exceptions.TransformationAlreadyPresentException;
import org.opentosca.toscana.core.api.exceptions.TransformationNotFoundException;
import org.opentosca.toscana.core.api.model.ArtifactResponse;
import org.opentosca.toscana.core.api.model.GetPropertiesResponse;
import org.opentosca.toscana.core.api.model.GetPropertiesResponse.PropertyWrap;
import org.opentosca.toscana.core.api.model.LogResponse;
import org.opentosca.toscana.core.api.model.SetPropertiesRequest;
import org.opentosca.toscana.core.api.model.SetPropertiesResponse;
import org.opentosca.toscana.core.api.model.TransformationResponse;
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
import org.opentosca.toscana.core.transformation.properties.Property;
import org.opentosca.toscana.core.transformation.properties.PropertyInstance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 This controller implements API operations regarding Transformations.
 <p>
 For sample Responses of the Requests, please have a look at docs/api/api_samples.md
 */
@CrossOrigin
@RestController
@RequestMapping("/csars/{csarName}/transformations")
public class TransformationController {

    private Logger log = LoggerFactory.getLogger(getClass());

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
     Accessed with http call <code>GET /csars/{csar}/transformatons/</code>
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
    public ResponseEntity<Resources<TransformationResponse>> getCSARTransformations(
        @PathVariable(name = "csarName") String name
    ) {
        Csar csar = findCsarByName(name);
        Link selfLink =
            linkTo(methodOn(TransformationController.class)
                .getCSARTransformations(name))
                .withSelfRel()
                .expand(name);
        List<TransformationResponse> transformations = new ArrayList<>();
        for (Map.Entry<String, Transformation> entry : csar.getTransformations().entrySet()) {
            transformations.add(new TransformationResponse(
                0,
                entry.getValue().getState().name(),
                entry.getKey(),
                csar.getIdentifier()
            ));
        }
        Resources<TransformationResponse> resources = new Resources<>(transformations, selfLink);
        return ResponseEntity.ok(resources);
    }

    /**
     Returns informations about a single transformation
     <p>
     Accessed with http call <code>GET /csars/{csar}/transformatons/{platform}/</code>
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
    public ResponseEntity<TransformationResponse> getCSARTransformation(
        @PathVariable(name = "csarName") String name,
        @PathVariable(name = "platform") String platform
    ) {
        Csar csar = findCsarByName(name);
        Transformation transformation = findTransformationByPlatform(csar, platform);
        return ResponseEntity.ok().body(new TransformationResponse(
            0,
            transformation.getState().name(),
            platform, name
        ));
    }

    /**
     Creates a new transformation for the given platform and csar <p>
     <p>
     Accessed with http call <code>PUT or POST /csars/{csar}/transformatons/{platform}/create</code>
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
    public ResponseEntity<TransformationResponse> addTransformation(
        @PathVariable(name = "csarName") String name,
        @PathVariable(name = "platform") String platform
    ) {
        log.info("Creating transformation for csar '{}' on '{}'", name, platform);
        Csar csar = findCsarByName(name);
        //Return bad Request if a transformation for this platform is already present
        if (csar.getTransformations().get(platform) != null) {
            throw new TransformationAlreadyPresentException();
        }
        //Return 404 if the platform does not exist
        Platform p = platformService.findPlatformById(platform);
        if (p == null) {
            throw new PlatformNotFoundException();
        }
        Transformation transformation = transformationService.createTransformation(csar, p);

//        if (transformation.getState() == TransformationState.READY) {
//            //TODO Replace with start query
//            transformationService.startTransformation(csar.getTransformations().get(platform));
//        }
        return ResponseEntity.ok().build();
    }

    /**
     This Mapping starts a transformation
     <p>
     Accessed with http call <code>GET /csars/{csar}/transformatons/{platform}/start</code>
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
     set properties</td>
     </tr>
     <tr>
     <td>404</td>
     <td>application/json</td>
     <td>Returns a error message if the csar is not found or if the csar does not have a transformation for the givenname
     (see returned error message for details)</td>
     </tr>
     </table>
     */
    @RequestMapping(
        path = "/{platform}/start",
        method = RequestMethod.POST,
        produces = "application/hal+json"
    )
    public ResponseEntity startTransformation(
        @PathVariable(name = "csarName") String name,
        @PathVariable(name = "platform") String platform
    ) {
        log.info("Starting transformation for csar '{}' on '{}'", name, platform);
        Csar csar = findCsarByName(name);
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
     Accessed with http call <code>DELETE /csars/{csar}/transformatons/{platform}/delete</code>
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
     <td>Returns a Empty body if the deletion was sucessful</td>
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
    public ResponseEntity<TransformationResponse> deleteTransformation(
        @PathVariable(name = "csarName") String name,
        @PathVariable(name = "platform") String platform
    ) {
        Csar csar = findCsarByName(name);
        Transformation transformation = findTransformationByPlatform(csar, platform);

        if (transformationService.deleteTransformation(transformation)) {
            // return 200 if the deletion was sucessful
            return ResponseEntity.ok().build();
        } else {
            // return 500 if the deletion failed
            return ResponseEntity.status(400).build();
        }
    }

    /**
     Returns the logs from a given start index to the current end of the log file. If the start index is higher then
     the current end index, a empty list is returned! The start parameter is a URL encoded parameter
     (<code>?start=0</code>)
     <p>
     Accessed with http call <code>GET /csars/{csar}/transformatons/{platform}/logs</code>
     <table summary="">
     <tr>
     <td>HTTP-Code</td>
     <td>Mime-Type</td>
     <td>Description (Returned if)</td>
     </tr>
     <tr>
     <td>200</td>
     <td>application/hal+json</td>
     <td>Returns a Json object containing the desired part of the log for the transformation</td>
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
    public ResponseEntity<LogResponse> getTransformationLogs(
        @PathVariable(name = "csarName") String name,
        @PathVariable(name = "platform") String platform,
        @RequestParam(name = "start", required = false, defaultValue = "0") Long start
    ) {
        Csar csar = findCsarByName(name);
        Transformation transformation = findTransformationByPlatform(csar, platform);
        Log log = transformation.getLog();
        List<LogEntry> entries = log.getLogEntries(Math.toIntExact(start));
        return ResponseEntity.ok().body(new LogResponse(
            start,
            start + entries.size() - 1, //TODO Maybe move this calculation into the LogResponse Class
            entries,
            platform,
            name
        ));
    }

    /**
     Returns the URL to download the target Artifact (Wrapped in json)<p>
     <p>
     Accessed with http call <code>GET /csars/{csar}/transformatons/{platform}/artifacts</code>
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
     <td>Returned if the transformation is not in a valid state (has to be in DONE or ERROR) to set properties</td>
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
        produces = "application/hal+json"
    )
    public ResponseEntity<ArtifactResponse> getTransformationArtifact(
        @PathVariable(name = "csarName") String name,
        @PathVariable(name = "platform") String platform
    ) {
        Csar csar = findCsarByName(name);
        Transformation transformation = findTransformationByPlatform(csar, platform);
        TargetArtifact artifact = transformation.getTargetArtifact();
        if (artifact == null) {
            throw new IllegalTransformationStateException("The transformation has not finished yet!");
        }
        return ResponseEntity.ok().body(new ArtifactResponse(artifact.getArtifactDownloadURL(), platform, name));
    }

    /**
     Returns a list of properties (key-value pairs) that might have to be set (if they are required) in order to start
     the transformation <p>
     Acessed with http call <code>GET /csars/{csar}/transformatons/{platform}/properties</code>
     <table summary="">
     <tr>
     <td>HTTP-Code</td>
     <td>Mime-Type</td>
     <td>Description (Returned if)</td>
     </tr>
     <tr>
     <td>200</td>
     <td>application/hal+json</td>
     <td>Returned with content body (See samples.md) if the operation was sucessful</td>
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
        path = "/{platform}/properties",
        method = RequestMethod.GET,
        produces = "application/hal+json"
    )
    public ResponseEntity<GetPropertiesResponse> getTransformationProperties(
        @PathVariable(name = "csarName") String name,
        @PathVariable(name = "platform") String platform
    ) {
        Csar csar = findCsarByName(name);
        Transformation transformation = findTransformationByPlatform(csar, platform);
        checkTransformationsForProperties(transformation, true);
        List<PropertyWrap> propertyWrapList = new ArrayList<>();
        //TODO add filtering depending on the transformation state (i.e. Transforming, Deploying...)
        PropertyInstance instance = transformation.getProperties();
        for (Property property : transformation.getProperties().getPropertySchema()) {
            propertyWrapList.add(new PropertyWrap(
                    property.getKey(),
                    property.getType().getTypeName(),
                    property.getDescription(),
                    instance.getPropertyValues().get(property.getKey()),
                    property.isRequired()
                )
            );
        }
        return ResponseEntity.ok(new GetPropertiesResponse(name, platform, propertyWrapList));
    }

    /**
     This Mapping is used to post the inputs (properties) to the server. this will set the properties internally
     <p>
     Accessed with http call <code>PUT or POST /csars/{csar}/transformatons/{platform}/properties</code>
     <table summary="">
     <tr>
     <td>HTTP-Code</td>
     <td>Mime-Type</td>
     <td>Description (Returned if)</td>
     </tr>
     <tr>
     <td>200</td>
     <td>application/hal+json</td>
     <td>Returns a empty body if all required properties have been set</td>
     </tr>
     <tr>
     <td>400</td>
     <td>application/json</td>
     <td>Returned if the transformation is not in a valid state (has to be in INPUT_REQUIRED) to set properties</td>
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
        path = "/{platform}/properties",
        method = {RequestMethod.POST, RequestMethod.PUT},
        produces = "application/json"
    )
    public ResponseEntity<SetPropertiesResponse> setTransformationProperties(
        @PathVariable(name = "csarName") String name,
        @PathVariable(name = "platform") String platform,
        @RequestBody SetPropertiesRequest setPropertiesRequest
    ) {
        Csar csar = findCsarByName(name);
        Transformation transformation = findTransformationByPlatform(csar, platform);
        checkTransformationsForProperties(transformation, false);
        Map<String, Boolean> sucesses = new HashMap<>();
        boolean somethingFailed = false;
        //Set The Properties and check their validity
        for (Map.Entry<String, String> entry : setPropertiesRequest.getProperties().entrySet()) {
            try {
                transformation.setProperty(entry.getKey(), entry.getValue());
                sucesses.put(entry.getKey(), true);
            } catch (Exception e) {
                somethingFailed = true;
                sucesses.put(entry.getKey(), false);
            }
        }
        //Return the result (with code 200 if all inputs were valid and 400 if at least 1 was invalid)
//        PropertyInstance instance = transformation.getProperties();
//        //TODO if other requirement types get used, this needs a change!
//        //Change state of the transformation to show the user that all required properties have been set
//        if(instance.allRequiredPropertiesSet(RequirementType.TRANSFORMATION)) {
//            //TODO Maybe a different method to change the state is needed!
//            transformation.setState(TransformationState.READY);
//        }
        if (!somethingFailed) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new SetPropertiesResponse(sucesses));
        }
    }

    /**
     Checks if the given transformation exists and is in the required state otherwise a
     TransformationNotFoundException or IllegalTransformationsStateException is thrown
     */
    private void checkTransformationsForProperties(Transformation transformation, boolean isGetProps) {
        if (transformation == null) {
            throw new TransformationNotFoundException();
        } else if (transformation.getState() != TransformationState.INPUT_REQUIRED && !isGetProps) {
            throw new IllegalTransformationStateException("The transformation is not in the INPUT_REQUIRED state");
        }
    }

    /**
     Uses the csar service to find the csar instance (and handles error management)
     */
    private Csar findCsarByName(String name) {
        Csar csar = csarService.getCsar(name);
        if (csar == null) {
            throw new CsarNotFoundException("No Csar with name '" + name + "# found!");
        }
        return csar;
    }

    /**
     Uses the csar to find the transformation instance (and handles error management)
     */
    private Transformation findTransformationByPlatform(Csar csar, String platform) {
        Transformation transformation = csar.getTransformations().get(platform);
        if (transformation == null) {
            throw new TransformationNotFoundException("The Csar '" + csar.getIdentifier() + "' does not have " +
                "a transformaiton for the platform '" + platform + "'.");
        }
        return transformation;
    }
}
