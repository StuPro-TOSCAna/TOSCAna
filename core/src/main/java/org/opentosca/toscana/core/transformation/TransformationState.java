package org.opentosca.toscana.core.transformation;

/**
 The state a transformation can be in.
 <p>
 For more information see <code>/docs/dev/core/transformation-states.md</code>
 */
public enum TransformationState {
    READY,
    INPUT_REQUIRED,
    TRANSFORMING,
    DONE,
    ERROR
}
