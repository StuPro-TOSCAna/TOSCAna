package org.opentosca.toscana.core.plugin.lifecycle;

import java.util.function.Predicate;

import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.util.ExceptionAwareVoidFunction;

/**
 This represents a wrapper for a execution phase e.g. the transform phase.
 A Phase gets defined with a Function and a name

 @param <LifecycleT> The type of the Transformation Lifecycle Interface,
 this is equal to the <code>LifecycleT</code> of the Plugin that implements the
 LifecycleAware Plugin Class. */
public class ExecutionPhase<LifecycleT extends TransformationLifecycle> {
    private String name;
    private ExceptionAwareVoidFunction<LifecycleT> function;
    private Predicate<TransformationContext> executionCheck;

    public ExecutionPhase(
        String name,
        ExceptionAwareVoidFunction<LifecycleT> function
    ) {
        this.name = name;
        this.function = function;
        this.executionCheck = (e) -> true;
    }

    public ExecutionPhase(
        String name,
        ExceptionAwareVoidFunction<LifecycleT> function,
        Predicate<TransformationContext> validation
    ) {
        this(name, function);
        this.executionCheck = validation;
    }

    public boolean shouldExecute(TransformationContext context) {
        return executionCheck.test(context);
    }

    /**
     Calls the given function

     @param lifecycle the lifecycle object to call the function on/with
     @throws Exception if you throw an exception inside of the Function, the transformation will fail.
     */
    public void execute(LifecycleT lifecycle) throws Exception {
        function.apply(lifecycle);
    }

    /**
     @return the display name of the execution phase
     */
    public String getName() {
        return name;
    }
}
