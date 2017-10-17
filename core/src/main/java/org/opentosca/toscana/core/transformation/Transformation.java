package org.opentosca.toscana.core.transformation;

import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.artifacts.TargetArtifact;
import org.opentosca.toscana.core.transformation.platform.Platform;

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

}
