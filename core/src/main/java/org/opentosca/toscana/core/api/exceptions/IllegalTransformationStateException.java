package org.opentosca.toscana.core.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.BAD_REQUEST,
    reason="This Operation is illegal, because the transformation is not in a valid state" +
        " to perform this operation!")
public class IllegalTransformationStateException extends RuntimeException{
    public IllegalTransformationStateException() {
    }

    public IllegalTransformationStateException(String message) {
        super(message);
    }

    public IllegalTransformationStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
