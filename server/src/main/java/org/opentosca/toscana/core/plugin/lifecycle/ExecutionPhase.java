package org.opentosca.toscana.core.plugin.lifecycle;

import java.util.function.Predicate;

import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.util.ExceptionAwareVoidFunction;

import io.swagger.annotations.ApiModel;
import org.slf4j.Logger;

/**
 This represents a wrapper for a execution phase e.g. the transform phase.
 A Phase gets defined with a Function and a name

 @param <LifecycleT> The type of the Transformation Lifecycle Interface,
 this is equal to the <code>LifecycleT</code> of the Plugin that implements the
 LifecycleAware Plugin Class. */
@ApiModel
public class ExecutionPhase<LifecycleT extends TransformationLifecycle> extends LifecyclePhase {

    private ExceptionAwareVoidFunction<LifecycleT> function;
    private Predicate<TransformationContext> executionCheck;

    public ExecutionPhase(
        String name,
        ExceptionAwareVoidFunction<LifecycleT> function,
        AbstractLifecycle lifecycle,
        Logger logger
    ) {
        super(name, lifecycle, logger);
        this.function = function;
        this.executionCheck = (e) -> true;
    }

    public ExecutionPhase(
        String name,
        ExceptionAwareVoidFunction<LifecycleT> function,
        Predicate<TransformationContext> validation,
        AbstractLifecycle lifecycle,
        Logger logger
    ) {
        this(name, function, lifecycle, logger);
        this.executionCheck = validation;
    }

    public boolean shouldExecute(TransformationContext context) {
        boolean shouldExecute = executionCheck.test(context);
        if (!shouldExecute && getState() == State.PENDING) {
            setState(State.SKIPPING);
        }
        return shouldExecute;
    }

    /**
     Calls the given function if the phase should execute. Else does nothing.

     @param lifecycle the lifecycle object to call the function on/with
     @throws Exception if you throw an exception inside of the Function, the transformation will fail.
     */
    public void execute(LifecycleT lifecycle) throws Exception {
        try {
            setState(State.EXECUTING);
            function.apply(lifecycle);
            setState(State.DONE);
        } catch (Exception e) {
            setState(State.FAILED);
            throw e;
        }
    }

    public void skip() {
        setState(State.SKIPPED);
    }
}
