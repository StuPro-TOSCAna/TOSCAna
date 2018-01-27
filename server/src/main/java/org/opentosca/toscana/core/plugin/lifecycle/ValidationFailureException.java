package org.opentosca.toscana.core.plugin.lifecycle;

/**
 This is the exception that gets thrown if the validation fails
 */
public class ValidationFailureException extends Exception {
    public ValidationFailureException(String message) {
        super(message);
    }
}
