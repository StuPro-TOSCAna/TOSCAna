package org.opentosca.toscana.model.nodedefinition;

public class WordPressDefinition extends WebApplicationDefinition {

    public final static String ADMIN_USER_PROPERTY = "admin_user";
    public final static String ADMIN_PASSWORD_PROPERTY = "admin_password";
    public final static String DB_HOST_PROPERTY = "db_host";

    public final static String HOST_REQUIREMENT = "host";
    
    static {
        MAPPINGS.put(HOST_REQUIREMENT, "databaseEndpoint");
    }
}
