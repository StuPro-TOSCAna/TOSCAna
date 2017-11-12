package org.opentosca.toscana.core.transformation.execution;

import java.io.File;
import java.io.IOException;

import org.opentosca.toscana.core.plugin.PluginService;
import org.opentosca.toscana.core.plugin.TransformationPlugin;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.TransformationState;
import org.opentosca.toscana.core.transformation.artifacts.ArtifactService;
import org.opentosca.toscana.core.transformation.artifacts.TargetArtifact;

import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.slf4j.Logger;

public class ExecutionTask implements Runnable {

    private final Transformation transformation;
    private final TransformationPlugin plugin;
    private final File csarContentDir;
    private final File transformationRootDir;
    private final String platformId;
    private final String csarId;
    private final ArtifactService artifactService;
    private final Logger log;

    private boolean failed = false;

    public ExecutionTask(
        Transformation transformation,
        ArtifactService ams,
        PluginService pluginService,
        File csarContentDir,
        File transformationRootDir
    ) {
        this.transformation = transformation;
        this.log = transformation.getLog().getLogger(getClass());
        this.plugin = pluginService.findPluginByPlatform(transformation.getPlatform());
        this.csarContentDir = csarContentDir;
        this.transformationRootDir = transformationRootDir;
        this.artifactService = ams;
        this.csarId = transformation.getCsar().getIdentifier();
        this.platformId = transformation.getPlatform().id;
    }

    @Override
    public void run() {
        log.info("Starting Transformation executor for {}/{}", csarId, platformId);
        transformation.setState(TransformationState.TRANSFORMING);
        transformationRootDir.mkdirs();
        transform();
        serveArtifact();
        transformation.setState(failed ? TransformationState.ERROR : TransformationState.DONE);
    }

    private void serveArtifact() {
        if (transformationRootDir.listFiles().length != 0) {
            try {
                log.info("Compressing transformation artifacts");
                TargetArtifact artifact = artifactService.serveArtifact(transformation);
                transformation.setTargetArtifact(artifact);
                log.info("Artifact archive ready for download");
            } catch (IOException e) {
                log.error("Failed to serve artifact archive for transformation {}/{}", csarId, platformId, e);
                failed = true;
            }
        } else {
            failed = true;
            log.error("Logfile missing! Not compressing target artifacts: Transformation generated no output files");
        }
    }

    private void transform() {
        try {
            TServiceTemplate template = transformation.getCsar().getTemplate().orElseThrow(() ->
                new IllegalStateException("TServiceTemplate is null, will not start transformation."));
            plugin.transform(new TransformationContext(csarContentDir, transformationRootDir,
                transformation.getLog(), template, transformation.getProperties()));
            transformation.setState(TransformationState.DONE);
        } catch (Exception e) {
            log.info("Transformation of {}/{} failed", csarId, platformId);
            log.error("Something went wrong while transforming", e);
            failed = true;
        }
    }
}
