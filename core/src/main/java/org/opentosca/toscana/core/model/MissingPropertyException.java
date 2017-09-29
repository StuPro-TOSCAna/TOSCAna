package org.opentosca.toscana.core.model;

/**
 * Indicates that supplied properties for a transformation did not match the
 * required properties of the target platform.
 */
public class MissingPropertyException extends Exception{
    public MissingPropertyException(String message){
        super(message);
    }
}
