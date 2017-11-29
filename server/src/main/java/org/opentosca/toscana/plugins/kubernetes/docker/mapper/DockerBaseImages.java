package org.opentosca.toscana.plugins.kubernetes.docker.mapper;

import static org.opentosca.toscana.plugins.kubernetes.docker.mapper.MapperConstants.DOCKER_HUB_URL;

public enum DockerBaseImages {
    UBUNTU("library", "ubuntu", DOCKER_HUB_URL),
    DEBIAN("library", "debian", DOCKER_HUB_URL),
    CENTOS("library", "centos", DOCKER_HUB_URL),
    ALPINE("library", "alpine", DOCKER_HUB_URL),
    BUSYBOX("library", "busybox", DOCKER_HUB_URL),
    OPEN_SUSE("library", "opensuse", DOCKER_HUB_URL);

    private final String username;
    private final String repository;
    private final String registry;

    DockerBaseImages(String username, String repository, String registry) {
        this.username = username;
        this.repository = repository;
        this.registry = registry;
    }

    public String getUsername() {
        return username;
    }

    public String getRepository() {
        return repository;
    }

    public String getRegistry() {
        return registry;
    }
}
