package org.opentosca.toscana.cli.restclient.model;

public class TransformationInput {

    private String key;
    private String type;
    private boolean valid;

    /**
     *
     * @param type
     * @param key
     */
    public TransformationInput(String key, String type) {
        this.key = key;
        this.type = type;
    }

    public TransformationInput(String key, boolean valid) {
        this.key = key;
        this.valid = valid;
    }

    public String getKey() {
        return key;
    }

    public String getType() {
        return type;
    }

    public boolean getValid() {
        return valid;
    }
}
