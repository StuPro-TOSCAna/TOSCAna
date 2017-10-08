package org.opentosca.toscana.core.csar;

import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.opentosca.toscana.core.transformation.Transformation;

import java.io.File;
import java.util.Map;

public interface Csar {

    /**
     * @return a map of all transformation objects of this csar.
     * This includes scheduled, ongoing and finished transformations.
     * Key of each map entry is the platform identifier of its particular transformation.
     */
    public Map<String,Transformation> getTransformations();

    /**
     * @return the identifier of the CSAR
     */
    public String getIdentifier();
    
    public TServiceTemplate getTemplate();
	
    public void setTemplate(TServiceTemplate template);

	/**
	 * @return the root directory of the unzipped CSAR artifact which belongs to this csar
	 */
	public File getRoot();
    
}
