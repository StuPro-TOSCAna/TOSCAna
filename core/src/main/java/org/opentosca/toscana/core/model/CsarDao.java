package org.opentosca.toscana.core.model;

import java.util.List;

public interface CsarDao {

    /**
     * Persists given CSAR.
     * If a CSAR with the same name already exists, updates the CSAR.
     */
    void update(Csar csar);

    /**
     * Deletes given CSAR.
     */
    void delete(Csar csar);

    /**
     * Returns a CSAR which matches given csarName, or null if no match was found.
     */
    Csar find(String csarName);

    /**
     * Returns a list of all CSARS.
     * @return
     */
    List<Csar> findAll();
}
