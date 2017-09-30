package org.opentosca.toscana.core.transformation;

import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.logging.Log;

import java.util.Set;

class TransformationImpl implements Transformation {

    private Csar app;
    private TransformationState state;
    private Platform targetPlatform;
    private Set<Property> properties;
    private Log log;
    private Set<TransformationListener> listeners;
    private TargetArtifact targetArtifact;

    /**
     * Creates a new transformation for given app to given targetPlatform.
     * @param csar the subject of transformation
     * @param targetPlatform the target platform
     * @param properties the user-supplied properties for this transformation
     */
    public TransformationImpl(Csar csar, Platform targetPlatform, Set<Property> properties) {
        this.app = app;
        this.targetPlatform = targetPlatform;
        this.properties = properties;
        this.log = new Log();
        // TODO
    }


    /**
     * If this instance is in state <code>QUEUED</code> or <code>TRANSFORMING</code>, transformation will abort.
     * If this instance is in any other state, nothing happens
     * After calling, this instance's state will be <code>FAILED</code>.
     */
    public void abort(){
        // TODO
        throw new UnsupportedOperationException();
    }

    public void setProperties(Set<Property> properties){
        // TODO maybe this needs to become setProperty(Property property)
        this.properties = properties;
    }

    /**
     * @return the state the transformation is currently in
     */
    public TransformationState getState(){
        // TODO
        throw new UnsupportedOperationException();
    }


    /**
     * Registers given listener for updates regarding this transformations state change.
     * On a state change, the listeners' hooks will be called in <b>arbitrary order</b>.
     * If the listener is already registered, nothing happens.
     * @param listener
     */
    public void setOnStateChange(TransformationListener listener){
        // TODO
        // hint: use IdentityHashMap for Listeners
        throw new UnsupportedOperationException();
    }

    /**
     * Removes given listener from the list of registered listener for this transformation.
     * If the listener is not registered, nothing happens.
     * @param listener listener to be removed from the set of listeners
     */
    public void removeOnStateChange(TransformationListener listener){
        // TODO
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the logs of this transformation
     */
    public Log getLog(){
        // TODO
        throw new UnsupportedOperationException();
    }


    /**
     * @return if this transformation object's state is <code>DONE</code>, returns the target artifact of the transformation.
     * Else returns null.
     */
    public TargetArtifact getTargetArtifact(){
        // TODO
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the target platform of this instance
     */
    public Platform getPlatform(){
        // TODO
        throw new UnsupportedOperationException();
    }
}
