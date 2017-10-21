package org.opentosca.toscana.core.transformation;

import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.transformation.artifacts.TargetArtifact;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.properties.PropertyInstance;
import org.opentosca.toscana.core.transformation.properties.RequirementType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Transformation {

    /**
     * @return the state the transformation is currently in
     */
    TransformationState getState();

    /**
     * Sets the state of the transformation, this is only temporary and will be removed later.
     *
     * @param state
     */
    void setState(TransformationState state);

    /**
     * Returns the target platform of this instance
     */
    Platform getPlatform();

    /**
     * Sets the value of the property with its given key.
     * Throws a IllegalArgumentException if a property with the given key cannot be found
     * or if the entered value is invalid
     */
    default void setProperty(String key, String value) {
        getProperties().setPropertyValue(key, value);
    }

    /**
     * @return Key(Property Name)-Value map of all properties that have been set explicitly!
     */
    PropertyInstance getProperties();

    /**
     * Checks if all Properties for the given Requirement type.
     *
     * @return true if all properties have been set and are valid, false otherwise
     */
    default boolean isAllPropertiesSet(RequirementType type) {
        return getProperties().allPropertiesSetForType(type);
    }

    /**
     * This operation builds a Transformation specific logger that will log its output into the Logging systen used to acces the logs by the rest api
     *
     * @param clazz
     * @return
     */
    default Logger getTransformationLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    /**
     * Creates a new Transfomation Context for the given transformation
     *
     * @return
     */
    default TransformationContext toTransformationContext() {
        return new TransformationContext(this,
            new PluginFileAccess(null, null));
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
     * @return Returns the underlying Csar of the transformation
     */
    Csar getCsar();

}
