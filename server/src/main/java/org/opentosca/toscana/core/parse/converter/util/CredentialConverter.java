package org.opentosca.toscana.core.parse.converter.util;

import org.opentosca.toscana.model.datatype.Credential;

import org.slf4j.Logger;

public class CredentialConverter {

    private final Logger logger;

    public CredentialConverter(Logger logger) {
        this.logger = logger;
    }

    public Credential convert(org.eclipse.winery.model.tosca.yaml.tosca.datatypes.Credential wineryCredential) {
        // TODO change credential's protocol field to be an enum
        try {
            Credential.TokenType tokenType = Credential.TokenType.valueOf(wineryCredential.getTokenType().toUpperCase());
            Credential credential = Credential
                .builder(wineryCredential.getToken())
                .protocol(wineryCredential.getProtocol())
                .keys(wineryCredential.getKeys())
                .type(tokenType)
                .user(wineryCredential.getUser())
                .build();
            return credential;
        } catch (IllegalArgumentException e) {
            logger.error("Unsupported token type '{}' specified in a credential assignment. Supported types: '{}'",
                wineryCredential.getTokenType(),
                Credential.TokenType.values(),
                e);
            // TODO throw something more precise, e.g. IllegalServiceTemplateException
            throw new IllegalArgumentException();
        }
    }
}
