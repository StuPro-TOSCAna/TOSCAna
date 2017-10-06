package org.opentosca.toscana.core.csar;

import java.io.InputStream;
import java.util.Collection;

public interface CsarService {


    /**
     * Creates a new Csar instance
     * @param name identifying name of csar, must match [a-z0-9_-]+
     * @param csarStream the actual cloud service archive as InputStream
     * @return the newly created Csar instance
     */
    Csar uploadCsar(String name, InputStream csarStream);

    /**
     * Deletes given Csar and all associated transformations from in-memory and persistence layer.
     * @return true if successful, false otherwise
     */
    boolean deleteCsar(Csar csar);

    /**
     * Returns all Csars currently managed by the application
     * @return
     */
    Collection<Csar> getCsars();

	/**
	 * Returns csar which identifier field matches given identifier.
	 * Returns null if no match is found
	 */
	Csar getCsar(String identifier);
}
