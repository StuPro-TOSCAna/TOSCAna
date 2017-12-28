package org.opentosca.toscana.model.artifact;

import java.net.URL;
import java.util.Objects;
import java.util.Optional;

import org.opentosca.toscana.model.DescribableEntity;
import org.opentosca.toscana.model.datatype.Credential;

import lombok.Builder;
import lombok.Data;

/**
 A repository defines a named external repository which contains deployment and
 implementation artifacts that are referenced within an operation.
 */

@Data
public class Repository extends DescribableEntity {

    /**
     The name of the repository.
     */
    private final String name;
    /**
     The URL used to access the repository.
     */
    private final URL url;

    /**
     The optional Credential used to authorize access to the repository.
     */
    private final Credential credential;

    @Builder
    protected Repository(String name, URL url, Credential credential, String description) {
        super(description);
        if (name.isEmpty()) {
            throw new IllegalArgumentException("A repository name must not be empty");
        }
        this.name = Objects.requireNonNull(name);
        this.url = Objects.requireNonNull(url);
        this.credential = credential;
    }

    /**
     @param name {@link #name}
     @param url  {@link #url}
     */
    public static RepositoryBuilder builder(String name, URL url) {
        return new RepositoryBuilder().name(name).url(url);
    }

    /**
     @return {@link #credential}
     */
    public Optional<Credential> getCredential() {
        return Optional.ofNullable(credential);
    }
}
