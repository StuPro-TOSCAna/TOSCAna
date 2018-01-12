package org.opentosca.toscana.core.transformation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;

import org.opentosca.toscana.api.exceptions.PlatformNotFoundException;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarDao;
import org.opentosca.toscana.core.transformation.artifacts.TargetArtifact;
import org.opentosca.toscana.core.transformation.platform.Platform;

/**
 Data Access Object for Transformation objects.
 */
public interface TransformationDao {

    /**
     Creates a new transformation for given csar and platform. If a transformation with this csar and platform already
     exists, also deletes the old transformation.

     @param csar     the csar on which the new transformation will be based on
     @param platform the target platform of the new transformation
     @return the new transformation
     @throws PlatformNotFoundException if given platform is not supported by any known plugin
     */
    Transformation create(Csar csar, Platform platform) throws PlatformNotFoundException;

    /**
     Deletes given transformation
     */
    void delete(Transformation transformation);

    /**
     Returns an optional transformation object which matches given csar and platform
     */
    Optional<Transformation> find(Csar csar, Platform platform);

    /**
     Returns all transformation objects which match given csar
     */
    List<Transformation> find(Csar csar);

    /**
     @return the root directory of given transformation
     */
    File getRootDir(Transformation transformation);

    /**
     @return the content directory of given transformation
     */
    File getContentDir(Transformation transformation);

    /**
     Returns the output stream for given transformation
     */
    TargetArtifact createTargetArtifact(Transformation transformation) throws FileNotFoundException;

    /**
     Do not call. Used for internal initialization
     */
    void setCsarDao(CsarDao csarDao);
}
