package org.opentosca.toscana.core.parse.converter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.node.RootNode;

import org.eclipse.winery.model.tosca.yaml.TNodeTemplate;
import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.eclipse.winery.model.tosca.yaml.TTopologyTemplateDefinition;

/**
 Contains logic to convert a {@link TServiceTemplate} to a {@link EffectiveModel}
 */
public class ModelConverter {

    public EffectiveModel convert(TServiceTemplate serviceTemplate) throws UnknownNodeTypeException {
        Set<RootNode> nodes = convertNodeTemplates(serviceTemplate.getTopologyTemplate());
        EffectiveModel model = new EffectiveModel(nodes);
        return model;
    }

    private Set<RootNode> convertNodeTemplates(TTopologyTemplateDefinition topology) throws UnknownNodeTypeException {
        Set<RootNode> nodes = new HashSet<>();
        Map<String, TNodeTemplate> templateMap = topology.getNodeTemplates();
        NodeConverter nodeConverter = new NodeConverter();
        for (Map.Entry<String, TNodeTemplate> entry : templateMap.entrySet()) {
            RootNode node = nodeConverter.convert(entry.getKey(), entry.getValue());
            nodes.add(node);
        }
        return nodes;
    }
}
