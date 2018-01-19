package org.opentosca.toscana.core.parse.converter.util;

import java.util.HashMap;
import java.util.Map;

import org.opentosca.toscana.model.node.Apache;
import org.opentosca.toscana.model.node.BlockStorage;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.ContainerApplication;
import org.opentosca.toscana.model.node.ContainerRuntime;
import org.opentosca.toscana.model.node.Database;
import org.opentosca.toscana.model.node.Dbms;
import org.opentosca.toscana.model.node.DockerApplication;
import org.opentosca.toscana.model.node.LoadBalancer;
import org.opentosca.toscana.model.node.MysqlDatabase;
import org.opentosca.toscana.model.node.MysqlDbms;
import org.opentosca.toscana.model.node.Nodejs;
import org.opentosca.toscana.model.node.ObjectStorage;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.node.SoftwareComponent;
import org.opentosca.toscana.model.node.WebApplication;
import org.opentosca.toscana.model.node.WebServer;
import org.opentosca.toscana.model.node.WordPress;

public class NodeTypeResolver {

    public static final String TOSCA_PREFIX = "tosca.nodes.";
    private static final Map<String, Class<? extends RootNode>> TYPE_MAP = new HashMap<>();

    static {
        put("Compute", Compute.class);
        put("Container.Application", ContainerApplication.class);
        put("Container.Runtime", ContainerRuntime.class);
        put("Storage", "BlockStorage", BlockStorage.class);
        put("Database", Database.class);
        put("Database.MySQL", MysqlDatabase.class);
        put("Container", "Application.Docker", DockerApplication.class);
        put("DBMS", Dbms.class);
        put("DBMS", "MySQL", MysqlDbms.class);
        put("LoadBalancer", LoadBalancer.class);
        put("Storage", "ObjectStorage", ObjectStorage.class);
        put("SoftwareComponent", SoftwareComponent.class);
        put("WebApplication", WebApplication.class);
        put("WebApplication", "WordPress", WordPress.class);
        put("WebServer", WebServer.class);
        put("WebServer", "Apache", Apache.class);
        put("WebServer", "Nodejs", Nodejs.class);
    }

    public static Class<? extends RootNode> resolve(String nodeType) {
        Class<? extends RootNode> type = TYPE_MAP.get(nodeType);
        if (type != null) {
            return type;
        } else {
            throw new UnsupportedOperationException(String.format("NodeType '%s' is not supported", nodeType));
        }
    }

    private static void put(String shorthandName, Class<? extends RootNode> clazz) {
        put(null, shorthandName, clazz);
    }

    private static void put(String prefix, String shorthandName, Class<? extends RootNode> clazz) {
        TYPE_MAP.put(shorthandName, clazz);
        prefix = (prefix == null) ? "" : prefix + ".";
        String fullURI = TOSCA_PREFIX + prefix + shorthandName;
        TYPE_MAP.put(fullURI, clazz);
    }
}
