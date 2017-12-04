package org.opentosca.toscana.plugins.kubernetes.exceptions;

import org.opentosca.toscana.model.visitor.UnsupportedTypeException;

public class UnsupportedOsTypeException extends UnsupportedTypeException {
    public UnsupportedOsTypeException(Class type) {
        super(type);
    }
}
