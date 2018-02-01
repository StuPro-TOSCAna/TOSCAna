package org.opentosca.toscana.core.parse;

/**
 Indicates that a tosca template violates the TOSCA Simple Profile 1.1 specification
 */
public class ToscaTemplateException extends RuntimeException {

    public ToscaTemplateException(String message) {
        super(message);
    }
}
