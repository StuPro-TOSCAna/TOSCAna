package org.opentosca.toscana.core.transformation;

/**
 The state a transformation can be in.
 <p> 
 For more information see <code>/docs/dev/core/transformation-states.md</code>
 */
public enum TransformationState {
    /**
     The transformation is in this state, once it has been created but it has not been started or scheduled
     */
    READY,
    TRANSFORMING,
    DONE,
    ERROR,
    INPUT_REQUIRED
}
