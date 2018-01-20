package org.opentosca.toscana.model.artifact;

import java.util.Optional;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.DescribableEntity;
import org.opentosca.toscana.model.datatype.Credential;
import org.opentosca.toscana.model.util.ToscaKey;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 A repository defines a named external repository which contains deployment and
 implementation artifacts that are referenced within an operation.
 */

@EqualsAndHashCode
@ToString
public class Repository extends DescribableEntity {

    /**
     The URL used to access the repository.
     */
    public static final ToscaKey<String> URL = new ToscaKey<>("url").required(true);

    /**
     The optional Credential used to authorize access to the repository.
     */
    public static ToscaKey<Credential> CREDENTIAL = new ToscaKey<>("credential");

    public Repository(MappingEntity entity) {
        super(entity);
    }

    /**
     @return {@link #CREDENTIAL}
     */
    public Optional<Credential> getCredential() {
        return Optional.ofNullable(get(CREDENTIAL));
    }

    /**
     Sets {@link #CREDENTIAL}
     */
    public Repository setCredential(Credential credential) {
        set(CREDENTIAL, credential);
        return this;
    }

    /**
     @return {@link #URL}
     */
    public String getUrl() {
        return get(URL);
    }
}
