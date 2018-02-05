package org.opentosca.toscana.core.transformation.properties;

/**
 Indicates that for a given key, no corresponding property was found.
 */
public class NoSuchPropertyException extends Exception {

    private final String key;

    public NoSuchPropertyException(String key) {
        super(String.format("InputProperty with key '%s' does not exist", key));
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
