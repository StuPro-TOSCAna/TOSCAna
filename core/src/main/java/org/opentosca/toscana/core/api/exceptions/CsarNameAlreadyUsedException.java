package org.opentosca.toscana.core.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 This exception is meant to be thrown by the controller to produce a response for the client. <p> It is ment to be
 thrown if you try to create a csar and the given name is already in use <p> The response code produced is 400
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "This csar name is already used!")
public class CsarNameAlreadyUsedException extends RuntimeException {
    public CsarNameAlreadyUsedException() {
    }

    public CsarNameAlreadyUsedException(String message) {
        super(message);
    }

    public CsarNameAlreadyUsedException(String message, Throwable cause) {
        super(message, cause);
    }
}
