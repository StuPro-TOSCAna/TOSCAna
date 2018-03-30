package org.opentosca.toscana.plugins.kubernetes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.opentosca.toscana.plugins.kubernetes.model.kuberesource.IKubernetesResource;
import org.opentosca.toscana.plugins.kubernetes.model.kuberesource.ResourceDeployment;
import org.opentosca.toscana.plugins.kubernetes.model.kuberesource.ResourceService;
import org.opentosca.toscana.plugins.kubernetes.model.transform.Pod;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 Creates Kubernetes Resources (Deployments and Services) for a given list of Pods
 */
public class ResourceFileCreator {

    private List<IKubernetesResource<?>> resources = new ArrayList<>();

    public ResourceFileCreator(Collection<Pod> pods) {
        for (Pod pod : pods) {
            resources.add(new ResourceDeployment(pod));

            //Create a Service if the Pod has exposed ports
            if (pod.getPorts().size() != 0) {
                resources.add(new ResourceService(pod));
            }
        }
        // Build the Resources
        resources.forEach(IKubernetesResource::build);
    }

    /**
     @return The Created Kubernetes Resources as YAML, representing the value, the key represents the unique name of the Resource
     @throws JsonProcessingException gets thrown if Marshalling of a Resource to YAML has failed
     */
    public HashMap<String, String> getResourceYaml() throws JsonProcessingException {
        HashMap<String, String> result = new HashMap<>();
        for (IKubernetesResource<?> resource : resources) {
            result.put(resource.getName(), resource.toYaml());
        }
        return result;
    }
}
