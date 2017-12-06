package org.opentosca.toscana.model.visitor;

public class UnsupportedTypeException extends UnsupportedOperationException {

    public UnsupportedTypeException(Class type) {
        super(String.format("Type '%s' is not supported", type.getName()));
    }

    public UnsupportedTypeException(String message) {
        super(message);
    }
}
