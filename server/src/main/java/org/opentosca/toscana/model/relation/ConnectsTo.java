package org.opentosca.toscana.model.relation;

import java.util.Optional;

import org.opentosca.toscana.model.datatype.Credential;
import org.opentosca.toscana.model.visitor.RelationshipVisitor;

import lombok.Builder;
import lombok.Data;

/**
 Represents a network connection relationship between two nodes.
 (TOSCA Simple Profile in YAML Version 1.1, p. 160)
 */
@Data
public class ConnectsTo extends RootRelationship {

    /**
     The optional credential used to present to the target endpoint for either authentication or authorization purposes.
     (TOSCA Simple Profile in YAML Version 1.1, p. 161)
     */
    private final Credential credential;

    public ConnectsTo() {
        super(null);
        credential = null;
    }

    @Builder
    protected ConnectsTo(Credential credential,
                         String description) {
        super(description);
        this.credential = credential;
    }

    /**
     @return {@link #credential}
     */
    public Optional<Credential> getCredential() {
        return Optional.ofNullable(credential);
    }

    @Override
    public void accept(RelationshipVisitor v) {
        v.visit(this);
    }

    public static class ConnectsToBuilder extends RootRelationshipBuilder {
    }
}
