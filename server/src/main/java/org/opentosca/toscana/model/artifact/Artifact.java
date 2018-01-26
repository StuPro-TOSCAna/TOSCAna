package org.opentosca.toscana.model.artifact;

import java.util.Optional;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.DescribableEntity;
import org.opentosca.toscana.model.util.ToscaKey;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 see TOSCA Specification V1.1 ch. 3.5.6
 <p>
 An artifact defines a filePath that can be used within an operation.
 */
@EqualsAndHashCode
@ToString
public class Artifact extends DescribableEntity {

    /**
     The path (relative or absolute) which can be used to locate the artifact’s filePath.
     */
    public static ToscaKey<String> FILE_PATH = new ToscaKey<>("file")
        .required(true);

    /**
     The optional external repository that contains the artifact.
     If set, the artifact is expected to be referenceable by its filePath within the repository.
     */
    public static ToscaKey<Repository> REPOSITORY = new ToscaKey<>("repository")
        .type(Repository.class);

    /**
     The optional path the artifact shall be deployed to within the target
     */
    public static ToscaKey<String> DEPLOY_PATH = new ToscaKey<>("deploy_path");

    public Artifact(MappingEntity entity) {
        super(entity);
    }

    /**
     @return {@link #FILE_PATH}
     */
    public String getFilePath() {
        return get(FILE_PATH);
    }

    /**
     Sets {@link #FILE_PATH}
     */
    public Artifact setFilePath(String filePath) {
        set(FILE_PATH, filePath);
        return this;
    }

    /**
     @return {@link #REPOSITORY}
     */

    public Optional<Repository> getRepository() {
        return Optional.ofNullable(get(REPOSITORY));
    }

    /**
     Sets {@link #REPOSITORY}
     */
    public Artifact setRepository(Repository repository) {
        set(REPOSITORY, repository);
        return this;
    }

    /**
     @return {@link #DEPLOY_PATH}
     */

    public Optional<String> getDeployPath() {
        return Optional.ofNullable(get(DEPLOY_PATH));
    }

    /**
     Sets {@link #DEPLOY_PATH}
     */
    public Artifact setDeployPath(String deployPath) {
        set(DEPLOY_PATH, deployPath);
        return this;
    }
}
