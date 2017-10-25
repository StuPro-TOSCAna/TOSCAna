package org.opentosca.toscana.core.transformation;

import java.io.File;
import java.util.List;

import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarDao;
import org.opentosca.toscana.core.transformation.platform.Platform;

/**
 * Data Access Object for Transformation objects.
 */
public interface TransformationDao {

    /**
     * Creates a new transformation for given csar and platform. If a transformation with this csar and platform already
     * exists, also deletes the old transformation.
     *
     * @param csar     the csar on which the new transformation will be based on
     * @param platform the target platform of the new transformation
     * @return the new transformation
     */
    Transformation create(Csar csar, Platform platform);

    /**
     * Deletes given transformation
     */
    void delete(Transformation transformation);

    /**
     * Returns all transformation objects which match given csar and plattform
     */
    Transformation find(Csar csar, Platform platform);

    /**
     * Returns all transformation objects which match given csar
     */
    List<Transformation> find(Csar csar);

    /**
     * @return the root directory of given transformation
     */
    File getRootDir(Transformation transformation);

    void setCsarDao(CsarDao csarDao);
}
