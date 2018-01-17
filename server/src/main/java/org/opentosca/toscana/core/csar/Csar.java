package org.opentosca.toscana.core.csar;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.properties.Property;
import org.opentosca.toscana.model.EffectiveModel;

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

    /**
     @return the identifier of the CSAR
     */
    String getIdentifier();

    /**
     @return the parsed EffectiveModel (wrapped in Optional) of the csar.
     <p>
     Empty if not parsed yet.
     */
    Optional<EffectiveModel> getModel();

    /**
     Returns model specific properties as a set. If there are none a empty set has to be returned. this must not
     return null to prevent problematic behaviour of the PropertyInstance class
     */
    Map<String, Property> getModelSpecificProperties();

    void setModel(EffectiveModel model);

    /**
     @return the log of this csar, which e.g. contains information about parsing
     */
    Log getLog();

    void setTransformations(List<Transformation> transformations);
}
