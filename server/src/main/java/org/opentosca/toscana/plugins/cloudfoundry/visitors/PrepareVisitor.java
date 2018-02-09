package org.opentosca.toscana.plugins.cloudfoundry.visitors;

import org.opentosca.toscana.model.node.MysqlDatabase;
import org.opentosca.toscana.model.visitor.StrictNodeVisitor;

import org.slf4j.Logger;

/**
 Prepare the model
 */
public class PrepareVisitor implements StrictNodeVisitor {

    public final static String CF_PREPARE_DB_PLACEHOLDER_USER = "cf_database_user_placeholder";
    public final static String CF_PREPARE_DB_PLACEHOLDER_NAME = "cf_database_name_placeholder";
    public final static String CF_PREPARE_DB_PLACEHOLDER_PW = "cf_database_password_placeholder";
    //yet not sure if to set the port here
    public final static int CF_PREPARE_DB_PLACEHOLDER_PORT = 3306;
    
    private final Logger logger;

    public PrepareVisitor(Logger logger){
        this.logger = logger;
    }

    @Override
    public void visit(MysqlDatabase node) {
        logger.debug("Prepare Model. Set placeholders for mysql database node");
        node.setUser(CF_PREPARE_DB_PLACEHOLDER_USER);
        node.setDatabaseName(CF_PREPARE_DB_PLACEHOLDER_NAME);
        node.setPassword(CF_PREPARE_DB_PLACEHOLDER_PW);
        node.setPort(CF_PREPARE_DB_PLACEHOLDER_PORT);
    }
}
