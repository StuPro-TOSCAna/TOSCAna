package org.opentosca.toscana.core.parse.converter.util;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.core.parse.model.ServiceGraph;
import org.opentosca.toscana.model.EntityId;

import com.google.common.collect.Lists;

/**
 Contains the IDs of common elements of the service template
 */
public class ToscaStructure {

    public static final EntityId SERVICE_TEMPLATE = new EntityId(Lists.newArrayList("service_template"));
    public static final EntityId REPOSITORIES = new EntityId(Lists.newArrayList("repositories"));
    public static final EntityId TOPOLOGY_TEMPLATE = new EntityId(Lists.newArrayList("topology_template"));
    public static final EntityId NODE_TEMPLATES = TOPOLOGY_TEMPLATE.descend("node_templates");
    public static final EntityId INPUTS = TOPOLOGY_TEMPLATE.descend("inputs");
    public static final EntityId OUTPUTS = TOPOLOGY_TEMPLATE.descend("outputs");

    public static void buildBasicStructure(ServiceGraph graph) {
        graph.addEntity(new MappingEntity(REPOSITORIES, graph));
        graph.addEntity(new MappingEntity(TOPOLOGY_TEMPLATE, graph));
        graph.addEntity(new MappingEntity(NODE_TEMPLATES, graph));
        graph.addEntity(new MappingEntity(INPUTS, graph));
        graph.addEntity(new MappingEntity(OUTPUTS, graph));
    }
}
