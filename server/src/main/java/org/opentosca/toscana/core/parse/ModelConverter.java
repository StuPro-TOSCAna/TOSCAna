package org.opentosca.toscana.core.parse;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.node.RootNode;

import org.eclipse.winery.model.tosca.yaml.TNodeType;
import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;

/**
 Contains logic to convert a {@link TServiceTemplate} to a {@link EffectiveModel}
 */
public class ModelConverter {
    
    public EffectiveModel convert(TServiceTemplate serviceTemplate) {
        Set<RootNode> nodes = convertNodeTemplates(serviceTemplate);
        EffectiveModel model = new EffectiveModel(nodes);
        return model;
    }

    private Set<RootNode> convertNodeTemplates(TServiceTemplate serviceTemplate) {
        Set<RootNode> nodes = new HashSet<>();
        Map<String, TNodeType> nodeMap = serviceTemplate.getNodeTypes();
        // TODO
        return nodes;
    }

}
