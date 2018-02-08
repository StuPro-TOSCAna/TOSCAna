package org.opentosca.toscana.core.parse.converter.util;

import java.util.HashMap;
import java.util.Map;

import org.opentosca.toscana.model.BaseToscaElement;
import org.opentosca.toscana.model.capability.AdminEndpointCapability;
import org.opentosca.toscana.model.capability.AttachmentCapability;
import org.opentosca.toscana.model.capability.BindableCapability;
import org.opentosca.toscana.model.capability.ComputeCapability;
import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.capability.DatabaseEndpointCapability;
import org.opentosca.toscana.model.capability.DockerContainerCapability;
import org.opentosca.toscana.model.capability.EndpointCapability;
import org.opentosca.toscana.model.capability.NetworkCapability;
import org.opentosca.toscana.model.capability.NodeCapability;
import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.model.capability.PublicEndpointCapability;
import org.opentosca.toscana.model.capability.ScalableCapability;
import org.opentosca.toscana.model.capability.StorageCapability;
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
import org.opentosca.toscana.model.node.SoftwareComponent;
import org.opentosca.toscana.model.node.WebApplication;
import org.opentosca.toscana.model.node.WebServer;
import org.opentosca.toscana.model.node.WordPress;
import org.opentosca.toscana.model.node.custom.JavaApplication;
import org.opentosca.toscana.model.node.custom.JavaRuntime;
import org.opentosca.toscana.model.relation.AttachesTo;
import org.opentosca.toscana.model.relation.ConnectsTo;
import org.opentosca.toscana.model.relation.DependsOn;
import org.opentosca.toscana.model.relation.HostedOn;
import org.opentosca.toscana.model.relation.RootRelationship;
import org.opentosca.toscana.model.relation.RoutesTo;

import static org.opentosca.toscana.core.parse.converter.util.TypeResolver.ElementType.CAPABILITY;
import static org.opentosca.toscana.core.parse.converter.util.TypeResolver.ElementType.NODE;
import static org.opentosca.toscana.core.parse.converter.util.TypeResolver.ElementType.RELATIONSHIP;

public class TypeResolver {

    private static final Map<String, Class<? extends BaseToscaElement>> TYPE_MAP = new HashMap<>();

    static {
        defineNodeMappings();
        defineCapabilityMappings();
        defineRelationshipMappings();
    }

    private static void defineNodeMappings() {
        put(NODE, "Compute", Compute.class);
        put(NODE, "Container.Application", ContainerApplication.class);
        put(NODE, "Container.Runtime", ContainerRuntime.class);
        put(NODE, "Storage", "BlockStorage", BlockStorage.class);
        put(NODE, "Database", Database.class);
        put(NODE, "Database.MySQL", MysqlDatabase.class);
        put(NODE, "Container", "Application.Docker", DockerApplication.class);
        put(NODE, "DBMS", Dbms.class);
        put(NODE, "DBMS", "MySQL", MysqlDbms.class);
        put(NODE, "LoadBalancer", LoadBalancer.class);
        put(NODE, "Storage", "ObjectStorage", ObjectStorage.class);
        put(NODE, "SoftwareComponent", SoftwareComponent.class);
        put(NODE, "WebApplication", WebApplication.class);
        put(NODE, "WebApplication", "WordPress", WordPress.class);
        put(NODE, "WebServer", WebServer.class);
        put(NODE, "WebServer", "Apache", Apache.class);
        put(NODE, "WebServer", "Nodejs", Nodejs.class);
        
        // custom types
        TYPE_MAP.put("toscana.nodes.JavaApplication", JavaApplication.class);
        TYPE_MAP.put("toscana.nodes.JavaRuntime", JavaRuntime.class);
    }

    private static void defineCapabilityMappings() {
        put(CAPABILITY, "Endpoint.Admin", AdminEndpointCapability.class);
        put(CAPABILITY, "Attachment", AttachmentCapability.class);
        put(CAPABILITY, "network.Bindable", BindableCapability.class);
        put(CAPABILITY, "Container", ContainerCapability.class);
        put(CAPABILITY, "Endpoint.Database", DatabaseEndpointCapability.class);
        put(CAPABILITY, "Container.Docker", DockerContainerCapability.class);
        put(CAPABILITY, "Endpoint", EndpointCapability.class);
        put(CAPABILITY, "Network", NetworkCapability.class);
        put(CAPABILITY, "Node", NodeCapability.class);
        put(CAPABILITY, "OperatingSystem", OsCapability.class);
        put(CAPABILITY, "Endpoint.Public", PublicEndpointCapability.class);
        put(CAPABILITY, "Scalable", ScalableCapability.class);
        put(CAPABILITY, "Storage", StorageCapability.class);

        // 'Compute' short notation clashes with the Compute node short notation
        // only normal notation is supported
        TYPE_MAP.put(CAPABILITY.prefix + "Compute", ComputeCapability.class);
    }

    private static void defineRelationshipMappings() {
        put(RELATIONSHIP, "AttachesTo", AttachesTo.class);
        put(RELATIONSHIP, "ConnectsTo", ConnectsTo.class);
        put(RELATIONSHIP, "DependsOn", DependsOn.class);
        put(RELATIONSHIP, "HostedOn", HostedOn.class);
        put(RELATIONSHIP, "Root", RootRelationship.class);
        put(RELATIONSHIP, "RoutesTo", RoutesTo.class);
    }

    public static Class<? extends BaseToscaElement> resolve(String type) {
        Class<? extends BaseToscaElement> clazz = TYPE_MAP.get(type);
        if (clazz != null) {
            return clazz;
        } else {
            throw new UnsupportedOperationException(String.format("Type '%s' is unknown and not supported", type));
        }
    }

    private static void put(ElementType type, String shorthandName, Class<? extends BaseToscaElement> clazz) {
        put(type, null, shorthandName, clazz);
    }

    private static void put(ElementType type, String prefix, String shorthandName, Class<? extends BaseToscaElement> clazz) {
        TYPE_MAP.put(shorthandName, clazz);
        prefix = (prefix == null) ? "" : prefix + ".";
        String fullURI = type.prefix + prefix + shorthandName;
        TYPE_MAP.put(fullURI, clazz);
    }

    public enum ElementType {
        NODE("nodes"),
        CAPABILITY("capabilities"),
        RELATIONSHIP("relationships");

        static final String TOSCA_PREFIX = "tosca";
        public final String prefix;

        ElementType(String prefix) {
            this.prefix = String.format("%s.%s.", TOSCA_PREFIX, prefix);
        }

    }
}
