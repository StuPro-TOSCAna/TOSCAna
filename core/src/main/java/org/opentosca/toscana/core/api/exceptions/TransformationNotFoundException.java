package org.opentosca.toscana.core.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND,
    reason="This Csar has no transformation for the given platform!")
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
