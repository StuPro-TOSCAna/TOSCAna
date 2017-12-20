package org.opentosca.toscana.core.parse.converter;

import org.opentosca.toscana.core.parse.InvalidCsarException;
import org.opentosca.toscana.core.transformation.logging.Log;

public class InvalidServiceTemplateException extends InvalidCsarException {

    public InvalidServiceTemplateException(Log log) {
        super(log);
    }
}
