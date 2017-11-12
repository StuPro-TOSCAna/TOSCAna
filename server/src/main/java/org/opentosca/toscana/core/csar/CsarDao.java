package org.opentosca.toscana.core.csar;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public interface CsarDao {

    /**
     Persists given CSAR inputstream. If a CSAR with the same name already exists, overwrites the old CSAR and all of
     its related transformations

     @param identifier  a unique identifier for the new csar
     @param inputStream an InputStream of a CSAR
     @return created csar
     */
    Csar create(String identifier, InputStream inputStream);

    /**
     Deletes CSAR which matches given identifier.
     */
    void delete(String identifier);

    /**
     Returns a optional CSAR instance which matches given csarName.
     If no match was found, the optional contains null.
     */
    Optional<Csar> find(String identifier);

    /**
     Returns a list of all csars.
     */
    List<Csar> findAll();

    /**
     @return the root dir of given csar
     */
    File getRootDir(Csar csar);

    /**
     @return the content directory of given csar
     */
    File getContentDir(Csar csar);

    /**
     @return the transformations dir of given csar
     */
    File getTransformationsDir(Csar csar);
}
