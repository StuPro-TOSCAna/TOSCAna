package org.opentosca.toscana.model.nodedefinition;

public class WebServerDefinition extends SoftwareComponentDefinition {

    public static final String DATA_ENDPOINT_CAPABILITY = "data_endpoint";
    public static final String ADMIN_ENDPOINT_CAPABILITY = "admin_endpoint";
    public static final String HOST_CAPABILITY = "host";

    static {
        MAPPINGS.put(HOST_CAPABILITY, "containerHost");
    }
}
