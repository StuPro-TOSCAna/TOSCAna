package org.opentosca.toscana.core.csar;

import java.util.Map;
import java.util.Set;

import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.properties.Property;

import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;

public interface Csar {

    /**
     @return a map of all transformation objects of this csar. This includes scheduled, ongoing and finished
     transformations. Key of each map entry is the platform identifier of its particular transformation.
     */
    public Map<String, Transformation> getTransformations();

    /**
     @return the identifier of the CSAR
     */
    public String getIdentifier();

    public TServiceTemplate getTemplate();

    /**
     Returns model specific properties as a set. If there are none a empty set has to be returned. this must not
     return null to prevent problematic behaviour of the PropertyInstance class
     */
    Set<Property> getModelSpecificProperties();

    public void setTemplate(TServiceTemplate template);

    /**
     @return the log of this csar, which e.g. contains information about parsing
     */
    public Log getLog();
}
