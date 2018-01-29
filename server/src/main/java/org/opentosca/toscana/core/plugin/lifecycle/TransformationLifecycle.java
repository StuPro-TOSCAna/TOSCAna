package org.opentosca.toscana.core.plugin.lifecycle;

/**
 The basic interface to implement the TransformationLifecycle
 */
public interface TransformationLifecycle {
    /**
     Checks if the Model (of the context that has been given during construction) is valid for this plugin
     That means the model gets checked for invalid properties such as unsupported Operating Systems or Architectures

     @return True if the vailidation succeeded, false otherwise.
     Returning false will cause the transformation to fail
     */
    boolean checkModel();

    /**
     Has to implement the prepare lifecycle operations
     <p>
     The Prepare lifecycle phase is described as follows:
     in this step the plugin processes the graph to transform it in the following step.
     For example the kubernetes plugin needs to split the graph into container and pods.
     <p>
     Exceptions have to be handled within the method, The transformation will abort if a runtime exception gets thrown
     out
     of the method. To abort the transformation yo have to execute <code>throw new RunntimeException()</code>
     */
    void prepare();

    /**
     Has to implement the transform lifecycle operations
     <p>
     The Transform Lifecycle phase is described as follows:
     this is where the real transformation is happening. I.e. the Resources for the target platform get created
     such as docker images, manifests...
     <p>
     Exceptions have to be handled within the method, The transformation will abort if a runtime exception gets thrown
     out
     of the method. To abort the transformation yo have to execute <code>throw new RunntimeException()</code>
     */
    void transform();

    /**
     Has to implement the clean (cleanup) lifecycle operations
     <p>
     Exceptions within this operation have to get caught, to prevent the failure of the transformation
     */
    void cleanup();

    /**

     */
    default void deploy() {
        throw new UnsupportedOperationException("This Plugin does not support deployments from within the TOSCAna Transformer!");
    }
}
