package org.opentosca.toscana.core.csar;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.util.LifecyclePhase;

public interface Csar {

    /**
     @return a map of all transformation objects of this csar. This includes scheduled, ongoing and finished
     transformations. Key of each map entry is the platform identifier of its particular transformation.
     */
    Map<String, Transformation> getTransformations();

    /**
     @param platformId the target platform id which will match the the returned transformation
     @return the transformation of this csar which match given platform
     */
    Optional<Transformation> getTransformation(String platformId);

    void setTransformations(List<Transformation> transformations);

    List<LifecyclePhase> getLifecyclePhases();

    LifecyclePhase getLifecyclePhase(Phase phase);

    /**
     @return the identifier of the CSAR
     */
    String getIdentifier();

    /**
     @return the log of this csar, which e.g. contains information about parsing
     */
    Log getLog();

    /**
     @return the root directory of the unzipped csar.
     */
    File getContentDir();

    enum Phase {
        UNZIP("unzip"),
        VALIDATE("validate"),
        PARSE("parse");

        private final String name;

        Phase(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
