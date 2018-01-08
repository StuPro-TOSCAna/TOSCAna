package org.opentosca.toscana.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 This exception is meant to be thrown by the controller to produce a response for the client. <p> This is meant to be
 thrown if you try to set or get the properties of a transformation that's not in the "INPUT_REQUIRED" state <p> The
 response code produced is 404
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST,
    reason = "This Operation is illegal, because the transformation is not in a valid state" +
        " to perform this operation!")
public class IllegalTransformationStateException extends RuntimeException {
    public IllegalTransformationStateException() {
    }

    public IllegalTransformationStateException(String message) {
        super(message);
    }

    public IllegalTransformationStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
