package org.opentosca.toscana.model.nodedefinition;

public class DbmsDefinition extends SoftwareComponentDefinition {

    public static final String ROOT_PASSWORD_PROPERTY = "root_password";

    public static final String HOST_CAPABILITY = "host";

    public static final String PORT_PROPERTY = "port";
    
    static {
        MAPPINGS.put(HOST_CAPABILITY, "containerHost");
    }

}
