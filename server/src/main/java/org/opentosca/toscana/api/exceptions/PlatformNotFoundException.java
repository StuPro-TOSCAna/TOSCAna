package org.opentosca.toscana.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 Gets thrown if an operations on a platform is requested, but there is no plugin supporting given platform <p> The
 produced response code is 404
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Platform not found")
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
