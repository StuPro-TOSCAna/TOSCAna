package org.opentosca.toscana.core.parse.converter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.node.RootNode;

import org.eclipse.winery.model.tosca.yaml.TNodeTemplate;
import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.eclipse.winery.model.tosca.yaml.TTopologyTemplateDefinition;
import org.slf4j.Logger;

/**
 Contains logic to convert a {@link TServiceTemplate} to a {@link EffectiveModel}
 */
public class ModelConverter {

    private final Logger logger;

    public ModelConverter(Logger logger) {
        this.logger = logger;
    }

    public EffectiveModel convert(TServiceTemplate serviceTemplate) throws UnknownNodeTypeException {
        Set<RootNode> nodes = convertNodeTemplates(serviceTemplate.getTopologyTemplate());
        return new EffectiveModel(nodes);
    }

    private Set<RootNode> convertNodeTemplates(TTopologyTemplateDefinition topology) throws UnknownNodeTypeException {
        Map<String, TNodeTemplate> templateMap;
        if (topology != null) {
            templateMap = topology.getNodeTemplates();
        } else {
            logger.warn("Topology template of service template is empty");
            templateMap = new HashMap<>();
        }

        Set<RootNode> nodes = new HashSet<>();
        NodeConverter nodeConverter = new NodeConverter();
        for (Map.Entry<String, TNodeTemplate> entry : templateMap.entrySet()) {
            RootNode node = nodeConverter.convert(entry.getKey(), entry.getValue());
            nodes.add(node);
        }
        return nodes;
    }
}
