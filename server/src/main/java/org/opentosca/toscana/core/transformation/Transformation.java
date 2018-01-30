package org.opentosca.toscana.core.transformation;

import java.util.Optional;

import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.transformation.artifacts.TargetArtifact;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.properties.PropertyInstance;
import org.opentosca.toscana.model.EffectiveModel;

public interface Transformation {

    /**
     @return the state the transformation is currently in.
     */
    TransformationState getState();

    /**
     Sets the state of the transformation, this is only temporary and will be removed later.
     */
    void setState(TransformationState state);

    /**
     Returns the target platform of this instance.
     */
    Platform getPlatform();

    /**
     Sets the value of the property with its given name.

     @throws IllegalArgumentException if a property with the given name cannot be found or if entered value is invalid
     */
    default void setProperty(String key, String value) {
        getInputs().setPropertyValue(key, value);
    }

    /**
     @return Key(SimpleProperty Name)-Value map of all properties that have been set explicitly!
     */
    PropertyInstance getInputs();

    /**
     Checks if all Properties for the given Requirement type are set.
     <p>
     This is just a "shortcut" for <code>getProperties().allPropertiesSet(type)</code>

     @return true if all properties have been set and are valid, false otherwise
     */
    default boolean allPropertiesSet() {
        return getInputs().allPropertiesSet();
    }

    /**
     Checks if all Properties for the given Requirement type are set.
     <p>
     This is just a "shortcut" for <code>getProperties().requiredPropertiesSet(type)</code>

     @return true if all required properties have been set and are valid, false otherwise
     */
    default boolean allRequiredPropertiesSet() {
        return getInputs().requiredPropertiesSet();
    }

    /**
     Returns the log of this transformation
     */
    Log getLog();

    /**
     If the transformation is finished, this will return a TargetArtifact object pointing to the generated target
     artifact otherwise returns null.
     */
    Optional<TargetArtifact> getTargetArtifact();

    void setTargetArtifact(TargetArtifact targetArtifact);

    /**
     @return Returns the underlying Csar of the transformation
     */
    Csar getCsar();

    /**
     Returns the underlying model of this transformation.
     */
    EffectiveModel getModel();

    /**
     Returns the outputs of the transformation.
     If the Transformation is not Done or has errored (The state is not DONE or ERROR)
     this will throw a IllegalStateExecption
     */
    PropertyInstance getOutputs() throws IllegalStateException;
}
