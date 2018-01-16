package org.opentosca.toscana.plugins.cloudfoundry.application;

/**
 provides attribute-names for the cloud foundry manifest
 */
public enum ManifestAttributes {
    APPLICATIONS_SECTION("applications"),
    SERVICE("services"),
    INSTANCES("instances"),
    NAME("name"),
    ENVIRONMENT("env"),
    DOMAIN("domain"),
    MULTIPLE_DOMAINS("domains"),
    MEMORY("mem_size"),
    BUILDPACK("buildpack"),
    COMMAND("command"),
    DISKSIZE("disk_quota"),
    DOCKER("docker"),
    DOCKER_IMAGE("image"),
    DOCKER_USERNAME("username"),
    HEALTHCHECK_TYPE("health-check-type"),
    HEALTHCHECK_ENDPOINT("health-check-http-endpoint"),
    HOST("host"),
    MULTIPLE_HOSTS("hosts"),
    NO_HOSTNAME("no-hostname"),
    NO_ROUTE("no-route"),
    MULTIPLE_ROUTES("routes"),
    PATH("path"),
    RANDOM_ROUTE("random-route"),
    STACK("stack"),
    TIMEOUT("timeout");

    private final String name;

    ManifestAttributes(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
