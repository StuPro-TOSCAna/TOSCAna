package org.opentosca.toscana.core.parse;

/**
 * Gets thrown whenever an invalid csar is encountered
 */
public class InvalidCsarException extends Exception {

    public InvalidCsarException(String msg) {
        super(msg);
    }
}
