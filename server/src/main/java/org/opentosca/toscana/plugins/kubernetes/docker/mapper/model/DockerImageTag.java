package org.opentosca.toscana.plugins.kubernetes.docker.mapper.model;

import java.util.Arrays;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vdurmont.semver4j.Semver;

/**
 This class represents a Tag for a Repository (image) on a docker registry.
 */
public class DockerImageTag {
    private String name;
    private Set<String> supportedArchitectures;

    public DockerImageTag(
        @JsonProperty("name") String name,
        @JsonProperty("supportedArchitectures") Set<String> supportedImageTags
    ) {
        this.name = name;
        this.supportedArchitectures = supportedImageTags;
    }

    public String getName() {
        return name;
    }

    public Set<String> getSupportedArchitectures() {
        return supportedArchitectures;
    }

    @JsonIgnore
    public boolean isVersionable() {
        try {
            new Semver(name, Semver.SemverType.LOOSE);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @JsonIgnore
    public Semver toVersion() {
        if (!isVersionable()) {
            throw new UnsupportedOperationException("The tag '" + name + "' is not versionable.");
        }
        return new Semver(name, Semver.SemverType.LOOSE);
    }

    public boolean isSupported(String architecture) {
        return supportedArchitectures.contains(architecture.toLowerCase());
    }

    @Override
    public String toString() {
        return "DockerImageTag(name='" + name + "', supportedArchitectures=" + Arrays.toString(supportedArchitectures.toArray()) + ")";
    }
}
