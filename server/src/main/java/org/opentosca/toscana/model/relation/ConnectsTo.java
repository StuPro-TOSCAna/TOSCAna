package org.opentosca.toscana.model.relation;

import java.util.Optional;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.datatype.Credential;
import org.opentosca.toscana.model.util.ToscaKey;
import org.opentosca.toscana.model.visitor.RelationshipVisitor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 Represents a network connection relationship between two nodes.
 (TOSCA Simple Profile in YAML Version 1.1, p. 160)
 */
@EqualsAndHashCode
@ToString
public class ConnectsTo extends RootRelationship {

    /**
     The optional credential used to present to the target endpoint for either authentication or authorization purposes.
     (TOSCA Simple Profile in YAML Version 1.1, p. 161)
     */
    public static ToscaKey<Credential> CREDENTIAL = new ToscaKey<>(PROPERTIES, "credential");

    public ConnectsTo(MappingEntity entity) {
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
    public void setCredential(Credential credential) {
        set(CREDENTIAL, credential);
    }

    @Override
    public void accept(RelationshipVisitor v) {
        v.visit(this);
    }
}
