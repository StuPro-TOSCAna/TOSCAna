package org.opentosca.toscana.core.csar;

import java.io.InputStream;
import java.util.List;

public interface CsarDao {

    /**
     * Persists given CSAR.
     * If a CSAR with the same name already exists, overwrites the old CSAR and all of its related transformations
     *
     * @param identifier  a unique idenfifier for the new csar
     * @param inputStream an InputStream of a CSAR
     * @return created csar
     */
    Csar create(String identifier, InputStream inputStream);

    /**
     * Deletes CSAR which matches given identifier.
     */
    void delete(String identifier);

    /**
     * Returns a CSAR which matches given csarName, or null if no match was found.
     */
    Csar find(String identifier);

    /**
     * Returns a list of all CSARS.
     *
     * @return
     */
    List<Csar> findAll();
    
}
