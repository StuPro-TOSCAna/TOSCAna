package org.opentosca.toscana.core.parse;

import java.util.HashSet;
import java.util.Set;

import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.node.RootNode;

import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;

public class ModelConverter {
    
    public EffectiveModel convert(TServiceTemplate serviceTemplate) {
        Set<RootNode> nodes = new HashSet<RootNode>();
        EffectiveModel model = new EffectiveModel(nodes);
        return model;
    }
}
