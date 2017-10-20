package org.opentosca.toscana.core.transformation;

import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.transformation.artifacts.TargetArtifact;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.properties.Property;
import org.opentosca.toscana.core.transformation.properties.PropertyInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

class TransformationImpl implements Transformation {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private Csar app;
	private TransformationState state = TransformationState.CREATED;
	private Platform targetPlatform;
	private PropertyInstance properties;
	private Log log;
	private TargetArtifact targetArtifact;

	/**
	 * Creates a new transformation for given app to given targetPlatform.
	 *
	 * @param csar           the subject of transformation
	 * @param targetPlatform the target platform
	 */
	public TransformationImpl(Csar csar, Platform targetPlatform) {
		this.app = csar;
		this.targetPlatform = targetPlatform;
		
		//Collect Possible Properties From the Platform and the Model
		Set<Property> properties = new HashSet<>();
		properties.addAll(csar.getModelSpecificProperties());
		properties.addAll(targetPlatform.getProperties());
		
		//Create property instance
		this.properties = new PropertyInstance(properties);
		
		//intialize internal log object
		this.log = new Log();
		// TODO
	}
	
	@Override
	public TransformationState getState() {
		return state;
	}

	@Override
	public void setState(TransformationState state) {
		this.state = state;
	}
	
	@Override
	public Csar getCsar() {
		return app;
	}

	@Override
	public Log getLog() {
		return log;
	}

	/**
	 * @return if this transformation object's state is <code>DONE</code>, returns the target artifact of the transformation.
	 * Else returns null.
	 */
	public TargetArtifact getTargetArtifact() {
		return targetArtifact;
	}

	@Override
	public Platform getPlatform() {
		return targetPlatform;
	}

	@Override
	public PropertyInstance getProperties() {
		return properties;
	}

}
