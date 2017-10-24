package org.opentosca.toscana.core.transformation;

/**
 * The state a transformation can be in.
 * <p>
 * For More information see <code>/docs/dev/core/transformation-states.md</code>
 */
public enum TransformationState {
    /**
     * The transformation is in this state, once it has been created but it has not been started or scheduled
     */
    CREATED,
    /**
     * The transformation is in this state, once the transformation has been scheduled
     * but the transformaion or deployment process was not instantiated yet.
     * <p>
     * If the Task performed is a transformation
     */
    READY,
    TRANSFORMING,
    DONE,
    ERROR,
    INTERUPTED,
    INPUT_REQUIRED
}
