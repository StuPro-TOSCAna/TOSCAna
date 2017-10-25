package org.opentosca.toscana.core.transformation.execution;

import org.opentosca.toscana.core.plugin.PluginService;
import org.opentosca.toscana.core.plugin.TransformationPlugin;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.TransformationState;
import org.opentosca.toscana.core.transformation.artifacts.ArtifactManagementService;
import org.slf4j.Logger;

import java.io.File;

public class ExecutionTask implements Runnable {

    private final Transformation transformation;
    private final TransformationPlugin plugin;
    private final File csarContentDir;
    private final File transformationRootDir;
    private ArtifactManagementService ams;

    private Logger log;

    public ExecutionTask(
        Transformation transformation,
        ArtifactManagementService ams,
        PluginService pluginService,
        File csarContentDir,
        File transformationRootDir
    ) {
        this.transformation = transformation;
        this.log = transformation.getLog().getLogger(getClass());
        this.plugin = pluginService.findPluginByPlatform(transformation.getPlatform());
        this.csarContentDir = csarContentDir;
        this.transformationRootDir = transformationRootDir;
        this.ams = ams;
    }

    @Override
    public void run() {
        log.info("Starting Transformation executor for {}/{}",
            transformation.getCsar().getIdentifier(),
            transformation.getPlatform().id);
        transformation.setState(TransformationState.TRANSFORMING);
        try {
            log.debug("Creating transformation root directory {}", transformationRootDir.getAbsolutePath());
            transformationRootDir.mkdirs();
            
            plugin.transform(new TransformationContext(transformation, csarContentDir, transformationRootDir));

            log.info("Compressing target artifacts");
            if (transformationRootDir != null && transformationRootDir.listFiles().length != 0) {
                String path = ams.saveToArtifactDirectory(
                    transformationRootDir,
                    transformation.getCsar().getIdentifier(),
                    transformation.getPlatform().id
                );
                log.info("Artifact is can be downloaded at relative url {}", path);
                //TODO Fix TargetArtifact not existing
                //transformation.getTargetArtifact().setArtifactDownloadURL(path);
            } else {
                log.info("No The transformation did not create any target artifacts");
            }
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
