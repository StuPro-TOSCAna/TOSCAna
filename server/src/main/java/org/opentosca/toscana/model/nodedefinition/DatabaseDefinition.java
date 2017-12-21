package org.opentosca.toscana.model.nodedefinition;

public class DatabaseDefinition extends AbstractDefinition {

    public final static String NAME_PROPERTY = "name";
    public final static String PORT_PROPERTY = "port";
    public final static String USER_PROPERTY = "user";
    public final static String PASSWORD_PROPERTY = "password";

    public final static String DATABASE_ENDPOINT_CAPABILITY = "database_endpoint";

    public final static String HOST_REQUIREMENT = "host";
    
    static {
        MAPPINGS.put(NAME_PROPERTY, "databaseName");
    }
}
