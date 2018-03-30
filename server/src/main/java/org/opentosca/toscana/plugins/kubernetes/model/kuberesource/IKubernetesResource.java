package org.opentosca.toscana.plugins.kubernetes.model.kuberesource;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 Describes a Kubernetes Resource

 @param <T> The Type of the Kubernetes Resource (this usualy is the class itself) */
public interface IKubernetesResource<T extends IKubernetesResource> {
    /**
     Constructs the KubernetesResource from the given input data.

     @return The method should return the itself (<code>return this;</code>)
     */
    T build();

    /**
     Converts the KubernetesResource into YAML format

     @return YAML String of the KubernetesResource
     @throws JsonProcessingException gets thrown if Marshalling of the Object has failed
     */
    String toYaml() throws JsonProcessingException;

    /**
     @return the unique name of the Kubernetes Resource
     */
    String getName();
}
