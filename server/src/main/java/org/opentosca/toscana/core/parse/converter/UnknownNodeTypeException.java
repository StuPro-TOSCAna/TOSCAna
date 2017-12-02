package org.opentosca.toscana.core.parse.converter;

public class UnknownNodeTypeException extends Exception {

    public UnknownNodeTypeException(String s) {
        super(s);
    }

    public UnknownNodeTypeException() {
        super();
    }
}
