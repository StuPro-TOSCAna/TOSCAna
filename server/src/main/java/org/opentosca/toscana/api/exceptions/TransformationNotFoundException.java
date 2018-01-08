package org.opentosca.toscana.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 This exception is meant to be thrown by the controller to produce a response for the client. <p> It is meant to be
 thrown if you try to perform operations with a Transformation and there is no Transformation with the given name <p>
 The response code produced is 404
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND,
    reason = "This Csar has no transformation for the given platform!")
public class TransformationNotFoundException extends RuntimeException {
    public TransformationNotFoundException() {
    }

    public TransformationNotFoundException(String message) {
        super(message);
    }

    public TransformationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
