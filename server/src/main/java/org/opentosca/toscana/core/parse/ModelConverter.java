package org.opentosca.toscana.core.parse;

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

    public EffectiveModel convert(TServiceTemplate serviceTemplate) {
        Set<RootNode> nodes = convertNodeTemplates(serviceTemplate.getTopologyTemplate());
        EffectiveModel model = new EffectiveModel(nodes);
        return model;
    }

    private Set<RootNode> convertNodeTemplates(TTopologyTemplateDefinition topology) {
        Set<RootNode> nodes = new HashSet<>();
        Map<String, TNodeTemplate> nodeMap = topology.getNodeTemplates();
        nodeMap.forEach((nodeName, node) -> nodes.add(convertNodeTemplate(nodeName, node)));
        return nodes;
    }

    private RootNode convertNodeTemplate(String nodeName, TNodeTemplate node) {
        switch (node.getType().getLocalPart()) {
            case(""):
                // TODO
                break;
        }
        return null;
    }

}
