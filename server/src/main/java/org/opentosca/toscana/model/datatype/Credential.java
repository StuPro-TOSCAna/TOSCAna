package org.opentosca.toscana.model.datatype;

import java.util.Map;
import java.util.Optional;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.BaseToscaElement;
import org.opentosca.toscana.model.util.ToscaKey;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 Describes authorization credentials used to access network accessible resources.
 <p>
 (TOSCA Simple Profile in YAML Version 1.1, p. 140)
 */
@EqualsAndHashCode
@ToString
public class Credential extends BaseToscaElement {

    /**
     The optional protocol name.
     <p>
     (TOSCA Simple Profile in YAML Version 1.1, p. 140)
     */
    public static ToscaKey<String> PROTOCOL = new ToscaKey<>("protocol");
    /**
     The optional token type. Defaults to {@link TokenType#PASSWORD}
     <p>
     (TOSCA Simple Profile in YAML Version 1.1, p. 140)
     */
    public static ToscaKey<TokenType> TYPE = new ToscaKey<>("type")
        .type(TokenType.class);
    /**
     The required token used as a credential for authorization or access to a networked resource.
     <p>
     (TOSCA Simple Profile in YAML Version 1.1, p. 140)
     */
    public static ToscaKey<String> TOKEN = new ToscaKey<>("token").required(true);
    /**
     Map of protocol-specific keys or assertions. Might be empty.
     <p>
     (TOSCA Simple Profile in YAML Version 1.1, p. 140)
     */
    public static ToscaKey<Map<String, String>> KEYS = new ToscaKey<>("keys");
    /**
     The optional user (name or ID) used for non-token based credentials.
     <p>
     (TOSCA Simple Profile in YAML Version 1.1, p.140)
     */
    public static ToscaKey<String> USER = new ToscaKey<>("user");

    public Credential(MappingEntity mappingEntity) {
        super(mappingEntity);
        setDefault(TYPE, TokenType.PASSWORD);
    }

    /**
     @return {@link #PROTOCOL}
     */
    public Optional<String> getProtocol() {
        return Optional.ofNullable(get(PROTOCOL));
    }

    /**
     Sets {@link #PROTOCOL}
     */
    public Credential setProtocol(String protocol) {
        set(PROTOCOL, protocol);
        return this;
    }

    /**
     @return {@link #TOKEN}
     */

    public String getToken() {
        return get(TOKEN);
    }

    /**
     Sets {@link #TOKEN}
     */
    public Credential setToken(String token) {
        set(TOKEN, token);
        return this;
    }

    /**
     @return {@link #USER}
     */

    public Optional<String> getUser() {
        return Optional.ofNullable(get(USER));
    }

    /**
     Sets {@link #USER}
     */
    public Credential setUser(String user) {
        set(USER, user);
        return this;
    }

    /**
     @return {@link #TYPE}
     */
    public TokenType getType() {
        return get(TYPE);
    }

    /**
     Sets {@link #TYPE}
     */
    public Credential setType(TokenType type) {
        set(TYPE, type);
        return this;
    }

    /**
     @return {@link #KEYS}
     */
    public Map<String, String> getKeys() {
        return get(KEYS);
    }

    /**
     Sets {@link #KEYS}
     */
    public Credential setKeys(Map<String, String> keys) {
        set(KEYS, keys);
        return this;
    }

    public enum TokenType {
        PASSWORD,
        // might grow
    }
}
