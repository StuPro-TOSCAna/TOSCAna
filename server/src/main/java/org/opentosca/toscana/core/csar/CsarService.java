package org.opentosca.toscana.core.csar;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.opentosca.toscana.core.parse.InvalidCsarException;

public interface CsarService {

    /**
     Creates a new Csar instance.

     @param identifier identifying name of csar, must match [a-z0-9_-]+
     @param csarStream the actual cloud service archive as InputStream
     @return the newly created Csar instance
     @throws InvalidCsarException if parsing, based on the content of given csarStream, failed The exception contains
     the parsing log)
     */
    Csar submitCsar(String identifier, InputStream csarStream) throws InvalidCsarException;

    /**
     Deletes given Csar and all associated transformations from in-memory and persistence layer.
     */
    void deleteCsar(Csar csar);

    /**
     Returns all Csars currently managed by the application.
     */
    List<Csar> getCsars();

    /**
     Returns an optional csar which identifier field matches given identifier.
     Note: The optional is null, if no matching csar is found.
     */
    Optional<Csar> getCsar(String identifier);
}
