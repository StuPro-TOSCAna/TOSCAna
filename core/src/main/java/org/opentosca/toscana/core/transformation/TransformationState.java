package org.opentosca.toscana.core.transformation;

/**
 * The state a transformation can be in.
 */
public enum TransformationState {
    INPUT_REQUIRED, READY, QUEUED, TRANSFORMING, DONE, FAILED
}
