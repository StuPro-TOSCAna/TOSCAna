package org.opentosca.toscana.plugins.lifecycle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opentosca.toscana.core.plugin.AbstractPlugin;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.platform.Platform;

import org.slf4j.Logger;

/**
 This class represents the base class for every Plugin that wants to implement the Transformation lifecycle

 @param <LifecycleT> The class type of the plugin specific implementation of the Lifecycle interface */
public abstract class LifecycleAwarePlugin<LifecycleT extends TransformationLifecycle>
    extends AbstractPlugin {

    /**
     Immutable list of execution tasks, they are in the right execution order and get executed from the first
     index to the last
     */
    private final List<ExecutionPhase<LifecycleT>> executionTasks;

    /**
     Initializes the plugin, that means that all tasks get added to a internal list (executionTasks) that then gets
     executed by the transform method.
     <p>
     The tasks get stored in a list (environment tasks)
     */
    public LifecycleAwarePlugin(Platform platform) {
        super(platform);
        List<ExecutionPhase<LifecycleT>> executionTasks = new ArrayList<>();

        //Add the execution tasks
        //Environment validation
        executionTasks.add(new ExecutionPhase<>("check environment", (e) -> {
            if (!checkEnvironment()) {
                throw new ValidationFailureException("Transformation Failed," +
                    " because the Environment check has failed!");
            }
        }));
        //Model validation
        executionTasks.add(new ExecutionPhase<>("check model", (e) -> {
            if (!e.checkModel()) {
                throw new ValidationFailureException("Transformation Failed," +
                    " because the model check has failed!");
            }
        }));

        //Transformation phases
        executionTasks.add(new ExecutionPhase<>("prepare", TransformationLifecycle::prepare));
        executionTasks.add(new ExecutionPhase<>("transformation", TransformationLifecycle::transform));
        executionTasks.add(new ExecutionPhase<>("cleanup", TransformationLifecycle::cleanup));

        //Make list immutable
        this.executionTasks = Collections.unmodifiableList(executionTasks);
    }

    /**
     Performs the execution of the phases in the order that has been defined during the construction of the object

     @param context context for the transformation
     */
    @Override
    public void transform(TransformationContext context) throws Exception {
        Logger logger = context.getLogger(getClass());

        //Store current time for time measurement
        long time = System.currentTimeMillis();

        logger.info("Building Lifecycle interface...");
        LifecycleT lifecycleInterface = getInstance(context);

        logger.info("This transformation has {} phases.", executionTasks.size());
        for (int i = 0; i < executionTasks.size(); i++) {
            ExecutionPhase<LifecycleT> phase = executionTasks.get(i);
            logger.info("Executing phase '{}' ({} of {})", phase.getName(), (i + 1), executionTasks.size());
            phase.execute(lifecycleInterface);
        }
        time = System.currentTimeMillis() - time;
        logger.info("The execution of the transformation was done after {} MS.", time);
    }

    /**
     Checks if all required environment parameters like installed CLIs, running services (such as docker) are available
     if so the method will return true, false otherwise (results in failure of the transformation)

     @return true if all env parameters are set, false otherwise
     */
    protected boolean checkEnvironment() {
        return true;
    }

    /**
     @param context The transformation context for which the Lifecycle interface should be built
     @return a newly constructed instance of the LifecycleInterface implemented by this plugin.
     */
    protected abstract LifecycleT getInstance(TransformationContext context) throws Exception;
}
