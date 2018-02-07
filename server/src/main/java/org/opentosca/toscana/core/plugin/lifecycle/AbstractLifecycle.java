package org.opentosca.toscana.core.plugin.lifecycle;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.util.LifecycleAccess;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

/**
 This class describes a base class that can be used by the plugins to implement
 their lifecycle. This already contains the context and a transformation specific logger
 */
public abstract class AbstractLifecycle implements TransformationLifecycle, LifecycleAccess {

    /**
     Declare transformation content specific directories name and paths
     */
    public static final String OUTPUT_DIR = "output/";
    public static final String OUTPUT_DIR_PATH = OUTPUT_DIR;
    public static final String SCRIPTS_DIR_NAME = "scripts/";
    public static final String SCRIPTS_DIR_PATH = OUTPUT_DIR_PATH + SCRIPTS_DIR_NAME;
    public static final String UTIL_DIR_NAME = "util/";
    public static final String UTIL_DIR_PATH = SCRIPTS_DIR_PATH + UTIL_DIR_NAME;
    public static final String RESOURCE_PATH_BASE = "/plugins/scripts/util/";

    /**
     The transformation specific logger that can be used to log to the transformations Log Object
     */
    protected Logger logger;
    /**
     The context on which the transformation should be transformed
     */
    protected TransformationContext context;

    private final List<ExecutionPhase> executionPhases;

    /**
     @param context because the context is always needed this should never be null
     It probably gets called by the <code>getInstance</code> method of the LifecycleAwarePlugin
     */
    public AbstractLifecycle(@NotNull TransformationContext context) throws IOException {
        this.logger = context.getLogger(getClass());
        logger.info("Building Lifecycle interface");
        this.context = context;
        this.logger = context.getLogger(getClass());
        PluginFileAccess access = context.getPluginFileAccess();
        setUpDirectories(access);
        this.executionPhases = populateExecutionPhases();
    }

    private void setUpDirectories(PluginFileAccess access) throws IOException {
        access.createDirectories(UTIL_DIR_PATH);

        //Iterate over all files in the script list
        List<String> files = IOUtils.readLines(
            getClass().getResourceAsStream(RESOURCE_PATH_BASE + "scripts-list"),
            Charsets.UTF_8
        );
        for (String line : files) {
            if (!line.isEmpty()) {
                //Copy the file into the desired directory
                InputStreamReader input = new InputStreamReader(
                    getClass().getResourceAsStream(RESOURCE_PATH_BASE + line)
                );
                BufferedWriter output = access.access(UTIL_DIR_PATH + line);
                IOUtils.copy(input, output);
                input.close();
                output.close();
            }
        }
    }

    private List<ExecutionPhase> populateExecutionPhases() {
        Logger phaseLogger = context.getLogger(ExecutionPhase.class);
        List<ExecutionPhase> executionPhases = new ArrayList<>();

        //Environment validation
        executionPhases.add(new ExecutionPhase<>("check environment", (e) -> {
            if (!checkEnvironment()) {
                throw new ValidationFailureException("Transformation Failed," +
                    " because the Environment check has failed!");
            }
        }, this, phaseLogger));
        //Model validation
        executionPhases.add(new ExecutionPhase<>("check model", (e) -> {
            if (!e.checkModel()) {
                throw new ValidationFailureException("Transformation Failed," +
                    " because the model check has failed!");
            }
        }, this, phaseLogger));
        //Transformation phases
        executionPhases.add(new ExecutionPhase<>("prepare", TransformationLifecycle::prepare, this, phaseLogger));
        executionPhases.add(new ExecutionPhase<>("transformation", TransformationLifecycle::transform, this, phaseLogger));
        executionPhases.add(new ExecutionPhase<>("cleanup", TransformationLifecycle::cleanup, this, phaseLogger));
        // Add Deployment Phase
        if (context.performDeployment()) {
            executionPhases.add(new ExecutionPhase<>(
                "deploy",
                TransformationLifecycle::deploy,
                this, phaseLogger
            ));
        }
        return Collections.unmodifiableList(executionPhases);
    }

    @Override
    public List<ExecutionPhase> getLifecyclePhases() {
        return executionPhases;
    }

    public TransformationContext getContext() {
        return context;
    }
}
