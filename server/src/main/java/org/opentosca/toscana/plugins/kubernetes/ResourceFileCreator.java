package org.opentosca.toscana.plugins.kubernetes;

import java.util.HashMap;
import java.util.List;

import org.opentosca.toscana.model.node.DockerApplication;
import org.opentosca.toscana.plugins.kubernetes.model.ResourceDeployment;
import org.opentosca.toscana.plugins.kubernetes.model.ResourceService;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceFileCreator {
    private final static Logger logger = LoggerFactory.getLogger(ResourceFileCreator.class);
    private List<List<DockerApplication>> stacks;
    private HashMap<String, String> result;

    public ResourceFileCreator(List<List<DockerApplication>> stacks) {
        this.stacks = stacks;
    }

    public HashMap<String, String> create() throws JsonProcessingException {
        result = new HashMap<>();
        for (List<DockerApplication> stack : stacks) {
            // TODO add dynamic naming
            ResourceDeployment replicationController
                = new ResourceDeployment("test", stack);
            ResourceService service = new ResourceService("test-service", "test");
            result.put("test-service", service.build().toYaml());
            result.put("test-deployment", replicationController.build().toYaml());
        }
        return result;
    }
}
