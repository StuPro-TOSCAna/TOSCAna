package org.opentosca.toscana.core.transformation;

import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.artifacts.TargetArtifact;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.properties.Property;
import org.opentosca.toscana.core.transformation.properties.RequirementType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public interface Transformation {

	/**
	 * @return the state the transformation is currently in
	 */
	TransformationState getState();

	/**
	 * Returns the target platform of this instance
	 */
	Platform getPlatform();

	/**
	 * Sets the value of the property with its given key.
	 * Throws a IllegalArgumentException if a property with the given key cannot be found
	 * or if the entered value is invalid
	 */
	void setProperty(String key, String value);

	/**
	 * @return Key(Property Name)-Value map of all properties that have been set explicitly!
	 */
	Map<String, String> getProperties();
	
	default boolean isAllPropertiesSet(RequirementType type) {
		Platform p = getPlatform();
		Map<String, String> propInstance = getProperties();
		for (Property property : p.getProperties()) {
			if(propInstance.get(property.getKey()) == null && property.getRequirementType() == type) {
				return false;
			}
		}
		return true;
	}
	
	default Logger getTransformationLogger(Class<?> clazz) {
		return LoggerFactory.getLogger(clazz);
	}

	/**
	 * Returns the log of this transformation
	 */
	Log getLog();

	/**
	 * If the transformation is finished, this will return a TargetArtifact
	 * object pointing to the generated target artifact otherwise it returns null!
	 */
	TargetArtifact getTargetArtifact();

	/**
	 * Registers given listener for updates regarding this transformations state change.
	 * On a state change, the listeners' hooks will be called in <b>arbitrary order</b>.
	 * If the listener is already registered, nothing happens.
	 *
	 * @param listener
	 */
	void setOnStateChange(TransformationListener listener);

	/**
	 * Removes given listener from the collection of registered listener for this transformation.
	 * If the listener is not registered, nothing happens.
	 *
	 * @param listener listener to be removed from the collection of listeners
	 */
	void removeOnStateChange(TransformationListener listener);
	
	Csar getCsar();

}
