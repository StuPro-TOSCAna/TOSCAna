package org.opentosca.toscana.model.datatype;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

/**
 Describes authorization credentials used to access network accessible resources.
 <p>
 (TOSCA Simple Profile in YAML Version 1.1, p. 140)
 */
@Data
public class Credential {

    /**
     The optional protocol name.
     <p>
     (TOSCA Simple Profile in YAML Version 1.1, p. 140)
     */
    private final String protocol;

    /**
     The token type. Defaults to {@link TokenType#PASSWORD}
     <p>
     (TOSCA Simple Profile in YAML Version 1.1, p. 140)
     */
    private final TokenType type;

    /**
     The required token used as a credential for authorization or access to a networked resource.
     <p>
     (TOSCA Simple Profile in YAML Version 1.1, p. 140)
     */
    private final String token;

    /**
     Set of protocol-specific keys or assertions. Might be empty.
     <p>
     (TOSCA Simple Profile in YAML Version 1.1, p. 140)
     */
    private final Map<String, String> keys;

    /**
     The optional user (name or ID) used for non-token based credentials.
     <p>
     (TOSCA Simple Profile in YAML Version 1.1, p.140)
     */
    private final String user;

    @Builder
    protected Credential(String protocol,
                         TokenType type,
                         String token,
                         @Singular Map<String, String> keys,
                         String user) {
        this.protocol = protocol;
        this.type = (type == null) ? TokenType.PASSWORD : type;
        this.token = Objects.requireNonNull(token);
        this.keys = Objects.requireNonNull(keys);
        this.user = user;
    }

    /**
     @param token {@link #token}
     */
    public static CredentialBuilder builder(String token) {
        return new CredentialBuilder().token(token);
    }

    /**
     @return {@link #protocol}
     */
    public Optional<String> getProtocol() {
        return Optional.ofNullable(protocol);
    }

    /**
     @return {@link #user}
     */
    public Optional<String> getUser() {
        return Optional.ofNullable(user);
    }

    public enum TokenType {
        PASSWORD,
        // might grow
    }
}
