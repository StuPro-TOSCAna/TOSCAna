package org.opentosca.toscana.plugins.kubernetes.exceptions;

import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.model.visitor.UnsupportedTypeException;

public class UnsupportedOsTypeException extends UnsupportedTypeException {
    public UnsupportedOsTypeException(OsCapability.Type type) {
        super(String.format("OS Type '%s' is not supported", type));
    }
}
