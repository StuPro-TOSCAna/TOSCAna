package org.opentosca.toscana.core.transformation;

import org.opentosca.toscana.core.logging.Log;

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
     * Returns the log of this transformation
     */
    public Log getLog();

    /**
     * Registers given listener for updates regarding this transformations state change.
     * On a state change, the listeners' hooks will be called in <b>arbitrary order</b>.
     * If the listener is already registered, nothing happens.
     * @param listener
     */
    void setOnStateChange(TransformationListener listener);

    /**
     * Removes given listener from the collection of registered listener for this transformation.
     * If the listener is not registered, nothing happens.
     * @param listener listener to be removed from the collection of listeners
     */
    void removeOnStateChange(TransformationListener listener);

}
