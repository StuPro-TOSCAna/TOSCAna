package org.opentosca.toscana.core.transformation;

/**
 * Is used in order to listen for state changes of a particular transformation.
 */
public interface TransformationListener {

    /**
     * Gets called whenever the observed transformation state changes.
     * <code>oldState</code> will never be the same as <code>newState</code>.
     * @param subject the observed transformation instance
     * @param oldState the state the transformation was in before the change
     * @param newState the new state, after the change happened
     */
    void onStateChange(Transformation subject, TransformationState oldState, TransformationState newState);
}
