package org.opentosca.toscana.core.plugin.lifecycle;

import java.util.function.Predicate;

import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.util.ExceptionAwareVoidFunction;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 This represents a wrapper for a execution phase e.g. the transform phase.
 A Phase gets defined with a Function and a name

 @param <LifecycleT> The type of the Transformation Lifecycle Interface,
 this is equal to the <code>LifecycleT</code> of the Plugin that implements the
 LifecycleAware Plugin Class. */
@ApiModel
public class ExecutionPhase<LifecycleT extends TransformationLifecycle> {

    private String name;
    private State state = State.PENDING;
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
            setState(State.ERROR);
            throw e;
        }
    }

    public void skip() {
        setState(State.SKIPPED);
    }

    /**
     @return the display name of the execution phase
     */
    @ApiModelProperty(
        required = true,
        notes = "the name of this execution phase",
        example = "deploy"
    )
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @ApiModelProperty(
        required = true,
        notes = "the current state of the phase. Must be one of { PENDING, SKIPPING, EXECUTING, SKIPPED, DONE, ERROR }",
        example = "PENDING"
    )
    @JsonProperty("state")
    public State getState() {
        return state;
    }

    private void setState(State state) {
        this.state = state;
    }

    public enum State {
        PENDING,
        SKIPPING,
        EXECUTING,
        SKIPPED,
        DONE,
        ERROR
    }
}
