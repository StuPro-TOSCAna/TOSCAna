package org.opentosca.toscana.core.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

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
