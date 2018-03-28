package org.opentosca.toscana.plugins.kubernetes.exceptions;

import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.model.visitor.UnsupportedTypeException;

/**
 This exception is used to make the unsupported Type Exception more fine grained.
 <p>
 It can be thrown if the OSType is unknown
 */
public class UnsupportedOsTypeException extends UnsupportedTypeException {
    public UnsupportedOsTypeException(OsCapability.Type type) {
        super(String.format("OS Type '%s' is not supported", type));
    }
}
