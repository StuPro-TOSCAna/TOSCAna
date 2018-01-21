package org.opentosca.toscana.plugins.kubernetes;

import java.util.Collection;
import java.util.HashMap;

import org.opentosca.toscana.plugins.kubernetes.model.Pod;
import org.opentosca.toscana.plugins.kubernetes.model.ResourceDeployment;
import org.opentosca.toscana.plugins.kubernetes.model.ResourceService;

import com.fasterxml.jackson.core.JsonProcessingException;

public class ResourceFileCreator {

    private Collection<Pod> pods;
    private HashMap<String, String> result;

    public ResourceFileCreator(Collection<Pod> pods) {
        this.pods = pods;
    }

    public HashMap<String, String> create() throws JsonProcessingException {
        result = new HashMap<>();
        for (Pod pod : pods) {
            ResourceDeployment replicationController
                = new ResourceDeployment(pod);
            ResourceService service = new ResourceService(pod);
            result.put(pod.getName() + "-deployment", replicationController.build().toYaml());
            result.put(pod.getName() + "-service", service.build().toYaml());
        }
        return result;
    }
}
