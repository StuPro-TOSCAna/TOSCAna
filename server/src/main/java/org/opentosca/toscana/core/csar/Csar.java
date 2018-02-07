package org.opentosca.toscana.core.csar;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.opentosca.toscana.core.plugin.lifecycle.LifecyclePhase;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.util.LifecycleAccess;

public interface Csar extends LifecycleAccess {

    /**
     the name of the directory which contains the unzipped content of the uploaded CSAR
     */
    String CONTENT_DIR = "content";
    
    /**
     Validates this csar. Caution: This call is expensive
     */
    boolean validate();

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
