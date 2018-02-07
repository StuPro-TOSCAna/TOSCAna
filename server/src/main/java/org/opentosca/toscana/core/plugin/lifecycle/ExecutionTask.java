package org.opentosca.toscana.core.plugin.lifecycle;

import java.io.File;
import java.io.IOException;

import org.opentosca.toscana.core.plugin.PluginService;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.TransformationState;
import org.opentosca.toscana.core.transformation.artifacts.ArtifactService;
import org.opentosca.toscana.core.transformation.artifacts.TargetArtifact;

import org.slf4j.Logger;

public class ExecutionTask implements Runnable {

    private final Transformation transformation;
    private final ToscanaPlugin plugin;
    private final File csarContentDir;
    private final File transformationRootDir;
    private final String platformId;
    private final String csarId;
    private final ArtifactService artifactService;
    private final Logger logger;

    private boolean failed = false;

    public ExecutionTask(
        Transformation transformation,
        ArtifactService ams,
        PluginService pluginService,
        File csarContentDir,
        File transformationRootDir
    ) {
        this.transformation = transformation;
        this.logger = transformation.getLog().getLogger(getClass());
        this.plugin = pluginService.findPluginByPlatform(transformation.getPlatform());
        this.csarContentDir = csarContentDir;
        this.transformationRootDir = transformationRootDir;
        this.artifactService = ams;
        this.csarId = transformation.getCsar().getIdentifier();
        this.platformId = transformation.getPlatform().id;
    }

    @Override
    public void run() {
        logger.info("Starting Transformation executor for {}/{}", csarId, platformId);
        transformation.setState(TransformationState.TRANSFORMING);
        transformationRootDir.mkdirs();
        transform();
        serveArtifact();
        transformation.setState(failed ? TransformationState.ERROR : TransformationState.DONE);
        transformation.getLog().close();
    }

    private void transform() {
        try {
            AbstractLifecycle lifecycle = plugin.getInstance(new TransformationContext(transformation, transformationRootDir));
            transformation.setLifecyclePhases(lifecycle.getLifecyclePhases());
            plugin.transform(lifecycle);
            transformation.setState(TransformationState.DONE);
        } catch (Exception e) {
            logger.info("Transformation of {}/{} failed", csarId, platformId);
            logger.error("Something went wrong while transforming", e);
            failed = true;
        }
    }

    private void serveArtifact() {
        if (transformationRootDir.listFiles().length != 0) {
            try {
                logger.info("Compressing transformation artifacts");
                TargetArtifact artifact = artifactService.serveArtifact(transformation);
                transformation.setTargetArtifact(artifact);
                logger.info("Artifact archive ready for download");
            } catch (IOException e) {
                logger.error("Failed to serve artifact archive for transformation {}/{}", csarId, platformId, e);
                failed = true;
            }
        } else {
            failed = true;
            logger.error("Logfile missing! Not compressing target artifacts: Transformation generated no output files");
        }
    }
}
