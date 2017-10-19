package org.opentosca.toscana.core.transformation;

import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.transformation.artifacts.TargetArtifact;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.properties.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class TransformationImpl implements Transformation {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private Csar app;
	private TransformationState state = TransformationState.CREATED;
	private Platform targetPlatform;
	private Set<Property> properties;
	private Map<String, String> propertyInstance = new HashMap<>();
	private Log log;
	private Set<TransformationListener> listeners;
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
		this.properties = targetPlatform.properties;
		this.log = new Log();
		// TODO
	}

//    public void setProperties(Set<Property> properties){
//        // TODO maybe this needs to become setProperty(Property property)
//        this.properties = properties;
//    }

	@Override
	public TransformationState getState() {
		return state;
	}

	@Override
	public void setState(TransformationState state) {
		this.state = state;
	}

	@Override
	public void setOnStateChange(TransformationListener listener) {
		// TODO
		// hint: use IdentityHashMap for Listeners
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeOnStateChange(TransformationListener listener) {
		// TODO
		throw new UnsupportedOperationException();
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
	public void setProperty(String key, String value) {
		logger.debug("Transformation '{}' for CSAR '{}': Trying to set Property '{}' to value: '{}'",
			targetPlatform.id, getCsar().getIdentifier(), key, value);
		for (Property p : targetPlatform.getProperties()) {
			if (p.getKey().equals(key)) {
				if (p.getType().validate(value)) {
					this.propertyInstance.put(key, value);
					return;
				} else {
					throw new IllegalArgumentException("The Property value is invalid!");
				}
			}
		}
		throw new IllegalArgumentException("A property with the given key does not exist! Key: " + key);
	}

	@Override
	public Map<String, String> getProperties() {
		return Collections.unmodifiableMap(propertyInstance);
	}
}
