package org.opentosca.toscana.plugins.kubernetes.docker.util;

import org.opentosca.toscana.core.transformation.TransformationContext;

import com.spotify.docker.client.messages.RegistryAuth;

import static org.opentosca.toscana.plugins.kubernetes.KubernetesPlugin.DOCKER_REGISTRY_PASSWORD_PROPERTY_KEY;
import static org.opentosca.toscana.plugins.kubernetes.KubernetesPlugin.DOCKER_REGISTRY_REPOSITORY_PROPERTY_KEY;
import static org.opentosca.toscana.plugins.kubernetes.KubernetesPlugin.DOCKER_REGISTRY_URL_PROPERTY_KEY;
import static org.opentosca.toscana.plugins.kubernetes.KubernetesPlugin.DOCKER_REGISTRY_USERNAME_PROPERTY_KEY;

/**
 Wraps credentials for a specific registry
 */
public class DockerRegistryCredentials {
    /**
     The URL to the registry
     <p>
     This Might be empty if DockerHub is used
     */
    private final String registryURL;
    /**
     The username to login to the registry
     */
    private final String username;
    /**
     The Corresponding password or access token
     */
    private final String password;
    /**
     The Name of the registry the images should be pushed to
     */
    private final String repository;

    public DockerRegistryCredentials(
        String registryURL,
        String username,
        String password,
        String repository
    ) {
        this.registryURL = registryURL;
        this.username = username;
        this.password = password;
        this.repository = repository;
    }

    /**
     Constructs a Docker Regitry credentials object from the properties defined through the context. 
     in the Kubernetes Plugin
     
     @throws java.util.NoSuchElementException gets thrown if one of the property keys is unknown or not set.
     */
    public static DockerRegistryCredentials fromContext(TransformationContext context) {
        return new DockerRegistryCredentials(
            context.getInputs().getOrThrow(DOCKER_REGISTRY_URL_PROPERTY_KEY),
            context.getInputs().getOrThrow(DOCKER_REGISTRY_USERNAME_PROPERTY_KEY),
            context.getInputs().getOrThrow(DOCKER_REGISTRY_PASSWORD_PROPERTY_KEY),
            context.getInputs().getOrThrow(DOCKER_REGISTRY_REPOSITORY_PROPERTY_KEY)
        );
    }

    public String getRegistryURL() {
        return registryURL;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRepository() {
        return repository;
    }

    /**
     Builds a RegistryAuth object for the DockerClient Library. This object is used to build a client with
     registry credentials
     */
    public RegistryAuth toRegistryAuth() {
        return RegistryAuth.builder()
            .username(username)
            .password(getPassword())
            .serverAddress(registryURL)
            .build();
    }
}
