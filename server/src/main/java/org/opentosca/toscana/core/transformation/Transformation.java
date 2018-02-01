package org.opentosca.toscana.core.transformation;

import java.util.List;
import java.util.Optional;

import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.transformation.artifacts.TargetArtifact;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.properties.PropertyInstance;
import org.opentosca.toscana.core.util.LifecyclePhase;
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
     @return Key(SimpleProperty Name)-Value map of all properties that have been set explicitly!
     */
    PropertyInstance getInputs();

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

    void setLifecyclePhases(List<LifecyclePhase> lifecyclePhases);

    List<LifecyclePhase> getLifecyclePhase();
}
