package org.opentosca.toscana.core.transformation.execution;

import org.opentosca.toscana.core.plugin.PluginService;
import org.opentosca.toscana.core.plugin.TransformationPlugin;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.TransformationService;
import org.opentosca.toscana.core.transformation.TransformationState;
import org.slf4j.Logger;

import java.io.File;

public class ExecutionTask implements Runnable {

    private final Transformation transformation;
    private final TransformationService transformationService;
    private final TransformationPlugin plugin;
    private final File csarContentDir;
    private final File transformationRootDir;

    
    private Logger log;

    public ExecutionTask(
        Transformation transformation,
        TransformationService transformationService,
        PluginService pluginService, File csarContentDir, File transformationRootDir
    ) {
        this.transformation = transformation;
        this.transformationService = transformationService;
        this.log = transformation.getLog().getLogger(getClass());
        this.plugin = pluginService.findPluginByPlatform(transformation.getPlatform());
        this.csarContentDir = csarContentDir;
        this.transformationRootDir = transformationRootDir;
    }

    @Override
    public void run() {
        log.info("Starting Transformation executor for {}/{}",
            transformation.getCsar().getIdentifier(),
            transformation.getPlatform().id);
        transformation.setState(TransformationState.TRANSFORMING);
        try {
            plugin.transform(new TransformationContext(transformation, csarContentDir, transformationRootDir));
        } catch (Exception e) {
            log.error("Transforming of {}/{} has errored!",
                transformation.getCsar().getIdentifier(),
                transformation.getPlatform().id);
            log.error("Error message: ", e);
            transformation.setState(TransformationState.ERROR);
            return;
        }
        transformation.setState(TransformationState.DONE);
        
    }
}
