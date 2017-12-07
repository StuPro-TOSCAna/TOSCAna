package org.opentosca.toscana.plugins.kubernetes;

import java.util.HashMap;
import java.util.List;

import org.opentosca.toscana.model.node.DockerApplication;
import org.opentosca.toscana.plugins.kubernetes.model.ResourceReplicationController;

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
            ResourceReplicationController replicationController
                = new ResourceReplicationController("test", stack);
            result.put("test-replication-controller", replicationController.build().toYaml());
        }
        return result;
    }
}
