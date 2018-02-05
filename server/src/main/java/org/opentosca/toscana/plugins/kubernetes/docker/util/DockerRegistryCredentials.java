package org.opentosca.toscana.plugins.kubernetes.docker.util;

import org.opentosca.toscana.core.transformation.TransformationContext;

import com.spotify.docker.client.messages.AuthConfig;

import static org.opentosca.toscana.plugins.kubernetes.KubernetesPlugin.DOCKER_REGISTRY_PASSWORD_PROPERTY_KEY;
import static org.opentosca.toscana.plugins.kubernetes.KubernetesPlugin.DOCKER_REGISTRY_REPOSITORY_PROPERTY_KEY;
import static org.opentosca.toscana.plugins.kubernetes.KubernetesPlugin.DOCKER_REGISTRY_URL_PROPERTY_KEY;
import static org.opentosca.toscana.plugins.kubernetes.KubernetesPlugin.DOCKER_REGISTRY_USERNAME_PROPERTY_KEY;

public class DockerRegistryCredentials {
    private final String registryURL;
    private final String username;
    private final String password;
    private final String repository;

    public DockerRegistryCredentials(String registryURL, String username, String password, String repository) {
        this.registryURL = registryURL;
        this.username = username;
        this.password = password;
        this.repository = repository;
    }

    /**
     Constructs a Docker Regitry credentials object from the properties defined through the context. in the Kubernetes Plugin
     */
    public static DockerRegistryCredentials fromContext(TransformationContext context) {
        return new DockerRegistryCredentials(
            context.getProperties().getOrThrow(DOCKER_REGISTRY_URL_PROPERTY_KEY),
            context.getProperties().getOrThrow(DOCKER_REGISTRY_USERNAME_PROPERTY_KEY),
            context.getProperties().getOrThrow(DOCKER_REGISTRY_PASSWORD_PROPERTY_KEY),
            context.getProperties().getOrThrow(DOCKER_REGISTRY_REPOSITORY_PROPERTY_KEY)
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

    public AuthConfig toAuthConfig() {
        return AuthConfig.builder()
            .username(username)
            .password(getPassword())
            .serverAddress(registryURL)
            .build();
    }
}
