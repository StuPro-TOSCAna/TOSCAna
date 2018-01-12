package org.opentosca.toscana.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Cannot delete csar with running transformations")
public class ActiveTransformationsException extends RuntimeException {
    public ActiveTransformationsException() {
    }

    public ActiveTransformationsException(String message) {
        super(message);
    }

    public ActiveTransformationsException(String message, Throwable cause) {
        super(message, cause);
    }
}
