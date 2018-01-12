package org.opentosca.toscana.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 This exception is meant to be thrown by the controller to produce a response for the client. <p> It is meant to be
 thrown if the given csar already has a transformation for this platform <p> The response code produced is 404
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST,
    reason = "A Transformation for this platform already exists!")
public class TransformationAlreadyPresentException extends RuntimeException {
    public TransformationAlreadyPresentException() {
    }

    public TransformationAlreadyPresentException(String message) {
        super(message);
    }

    public TransformationAlreadyPresentException(String message, Throwable cause) {
        super(message, cause);
    }
}
