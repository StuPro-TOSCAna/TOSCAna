package org.opentosca.toscana.plugins.kubernetes;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.opentosca.toscana.plugins.kubernetes.model.ResourceDeployment;
import org.opentosca.toscana.plugins.kubernetes.model.ResourceService;
import org.opentosca.toscana.plugins.kubernetes.util.NodeStack;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceFileCreator {
    private final static Logger logger = LoggerFactory.getLogger(ResourceFileCreator.class);
    private Set<NodeStack> stacks;
    private HashMap<String, String> result;

    public ResourceFileCreator(Set<NodeStack> stacks) {
        this.stacks = stacks;
    }

    public HashMap<String, String> create() throws JsonProcessingException {
        result = new HashMap<>();
        for (NodeStack stack : stacks) {
            ResourceDeployment replicationController
                = new ResourceDeployment(stack);
            ResourceService service = new ResourceService(stack);
            result.put(stack.getStackName() + "-deployment", replicationController.build().toYaml());
            result.put(stack.getStackName() + "-service", service.build().toYaml());
        }
        return result;
    }
}
