package org.opentosca.toscana.model.artifact;

import java.util.Objects;
import java.util.Optional;

import org.opentosca.toscana.model.DescribableEntity;

import lombok.Builder;
import lombok.Data;

/**
 see TOSCA Specification V1.1 ch. 3.5.6
 <p>
 An artifact defines a filePath that can be used within an operation.
 */
@Data
public class Artifact extends DescribableEntity {

    private final String name;

    /**
     The path (relative or absolute) which can be used to locate the artifact’s filePath.
     */
    private final String filePath;

    /**
     The optional external repository that contains the artifact.
     If set, the artifact is expected to be referenceable by its filePath within the repository.
     */
    private final Repository repository;

    /**
     The optional path the artifact shall be deployed to within the target
     */
    private final String deployPath;

    @Builder
    protected Artifact(String name,
                       String filePath,
                       Repository repository,
                       String deployPath,
                       String description) {
        super(description);
        this.name = Objects.requireNonNull(name);
        this.filePath = Objects.requireNonNull(filePath);
        this.repository = repository;
        this.deployPath = deployPath;
    }

    /**
     @param name     {@link #name}
     @param filePath {@link #filePath}
     */
    public static ArtifactBuilder builder(String name,
                                          String filePath) {
        return new ArtifactBuilder()
            .name(name)
            .filePath(filePath);
    }

    /**
     @return {@link #repository}
     */
    public Optional<Repository> getRepository() {
        return Optional.ofNullable(repository);
    }

    /**
     @return {@link #deployPath}
     */
    public Optional<String> getDeployPath() {
        return Optional.ofNullable(deployPath);
    }
}
