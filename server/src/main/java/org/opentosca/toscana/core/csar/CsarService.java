package org.opentosca.toscana.core.csar;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public interface CsarService {

    /**
     Creates a new Csar instance.

     @param identifier identifying name of csar, must match [a-z0-9_-]+
     @param csarStream the actual cloud service archive as InputStream
     @return the newly created Csar instance
     @throws CsarIdNotUniqueException if a csar with the same identifier already exists
     */
    Csar submitCsar(String identifier, InputStream csarStream) throws CsarIdNotUniqueException;

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
