package org.opentosca.toscana.core.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This exception is meant to be thrown by the controller to produce a response for the client.
 * <p>
 * It is ment to be thrown if you try to perform operations with a platform and there is no platform with the given identifier
 * <p>
 * The response code produced is 404
 */
@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="Platform not found")
public class PlatformNotFoundException extends RuntimeException {
    public PlatformNotFoundException() {
    }

    public PlatformNotFoundException(String message) {
        super(message);
    }

    public PlatformNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
