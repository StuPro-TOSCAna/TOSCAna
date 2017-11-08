package org.opentosca.toscana.core.parse;

import org.opentosca.toscana.core.transformation.logging.Log;

/**
 Gets thrown whenever an invalid csar is encountered
 */
public class InvalidCsarException extends Exception {

    private final Log log;

    public InvalidCsarException(Log log) {
        super();
        this.log = log;
    }

    public Log getLog() {
        return log;
    }
}
