package org.opentosca.toscana.core.plugin.lifecycle;

import java.util.List;
import java.util.Objects;

import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.platform.Platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 This class represents the base class for every Plugin that wants to implement the Transformation lifecycle

 @param <LifecycleT> The class type of the plugin specific implementation of the Lifecycle interface */
public abstract class ToscanaPlugin<LifecycleT extends AbstractLifecycle> {

    /**
     Immutable list of execution tasks, they are in the right execution order and get executed from the first
     index to the last
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Platform platform;

    /**
     Initializes the plugin, that means that all tasks get added to a internal list (executionPhases) that then gets
     executed by the transform method.
     <p>
     The tasks get stored in a list (environment tasks)
     */
    public ToscanaPlugin(Platform platform) {
        this.platform = Objects.requireNonNull(platform, "The platform is not allowed to be null");
        initPlugin();
    }

    private void initPlugin() {
        logger.info("Initializing plugin '{}'", this.platform.name);
        this.init();
        logger.info("Initialized plugin '{}'", this.platform.name);
    }

    /**
     Is called during the initialisation of the plugin
     */
    protected void init() {
        //Empty method, allows plugin to run code on initialisation
    }

    public Platform getPlatform() {
        return platform;
    }

    /**
     This method will transform  given model (contained in the TransformationContext instance) and will store the result
     in a directory provided by the context.
     <p>
     Performs the execution in phases in the order that has been defined during the construction of the object
     */
    public void transform(LifecycleT lifecycle) throws Exception {
        TransformationContext context = lifecycle.getContext();
        Logger logger = context.getLogger(getClass());
        long time = System.currentTimeMillis();

        List<ExecutionPhase> phases = lifecycle.getLifecyclePhases();

        int taskCount = countExecutionPhases(context, phases);
        logger.info("This transformation has {} phases", taskCount);

        for (int i = 0; i < phases.size(); i++) {
            ExecutionPhase phase = phases.get(i);
            if (phase.shouldExecute(context)) {
                logger.info("Executing phase '{}' ({} of {})", phase.getName(), (i + 1), taskCount);
                phase.execute(lifecycle);
            } else {
                phase.skip();
                logger.info("Skipping phase '{}' ({} of {})", phase.getName(), (i + 1), taskCount);
            }
        }
        time = System.currentTimeMillis() - time;
        logger.info("Transformation finished after {} ms", time);
    }

    private int countExecutionPhases(TransformationContext ctx, List<ExecutionPhase> phases) {
        return (int) phases.stream().filter(e -> e.shouldExecute(ctx)).count();
    }

    /**
     @param context The transformation context for which the Lifecycle interface should be built
     @return a newly constructed instance of the LifecycleInterface implemented by this plugin.
     */
    public abstract LifecycleT getInstance(TransformationContext context) throws Exception;
}
