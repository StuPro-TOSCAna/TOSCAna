package org.opentosca.toscana.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 This exception is meant to be thrown by the controller to produce a response for the client. <p> It is meant to be
 thrown if you try to perform operations with a csar and there is no csar with the given name <p> The response code
 produced is 404
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such Csar")
public class CsarNotFoundException extends RuntimeException {
    public CsarNotFoundException() {
    }

    public CsarNotFoundException(String message) {
        super(message);
    }

    public CsarNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
