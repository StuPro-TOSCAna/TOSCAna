package org.opentosca.toscana.core.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.BAD_REQUEST,
    reason="A Transformation for this platform already exists!")
public class TransformationAlreadyPresentException extends RuntimeException{
    public TransformationAlreadyPresentException() {
    }

    public TransformationAlreadyPresentException(String message) {
        super(message);
    }

    public TransformationAlreadyPresentException(String message, Throwable cause) {
        super(message, cause);
    }
}
