package org.opentosca.toscana.plugins.kubernetes.docker.mapper.model;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.opentosca.toscana.plugins.kubernetes.docker.mapper.DockerBaseImages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 This class represents a repository on a Docker Registry with its corresponding tags (Docker Image Tags)
 */
public class DockerImage {
    private String username;
    private String repository;
    private String registry;
    private List<DockerImageTag> tags;

    @JsonCreator
    public DockerImage(
        @JsonProperty("username") String username,
        @JsonProperty("repository") String repository,
        @JsonProperty("registry") String registry,
        @JsonProperty("tags") List<DockerImageTag> tags
    ) {
        this.username = username;
        this.repository = repository;
        this.registry = registry;
        this.tags = tags;
    }

    public DockerImage(DockerBaseImages image, List<DockerImageTag> tags) {
        this(image.getUsername(), image.getRepository(), image.getRegistry(), tags);
    }

    public String getUsername() {
        return username;
    }

    public String getRepository() {
        return repository;
    }

    public List<DockerImageTag> getTags() {
        return tags;
    }

    @JsonIgnore
    public Optional<DockerImageTag> findTagByName(String name) {
        return tags.stream().filter(e -> e.getName().equalsIgnoreCase(name)).findFirst();
    }

    @JsonIgnore
    public List<DockerImageTag> getVersionableTags() {
        return tags.stream().filter(DockerImageTag::isVersionable).collect(Collectors.toList());
    }

    public String getRegistry() {
        return registry;
    }
}
