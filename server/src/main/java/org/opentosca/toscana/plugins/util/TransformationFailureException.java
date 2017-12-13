package org.opentosca.toscana.plugins.util;

public class TransformationFailureException extends RuntimeException {
    public TransformationFailureException() {
    }

    public TransformationFailureException(String message) {
        super(message);
    }

    public TransformationFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
