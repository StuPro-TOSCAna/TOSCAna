package org.opentosca.toscana.core.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="No such Csar")
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
