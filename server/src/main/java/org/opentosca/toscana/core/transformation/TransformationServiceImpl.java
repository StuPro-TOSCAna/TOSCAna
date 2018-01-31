package org.opentosca.toscana.core.transformation;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.opentosca.toscana.api.exceptions.PlatformNotFoundException;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarDao;
import org.opentosca.toscana.core.plugin.PluginService;
import org.opentosca.toscana.core.transformation.artifacts.ArtifactService;
import org.opentosca.toscana.core.transformation.execution.ExecutionTask;
import org.opentosca.toscana.core.transformation.platform.Platform;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class TransformationServiceImpl implements TransformationService {

    private final TransformationDao transformationDao;
    private final CsarDao csarDao;
    private final PluginService pluginService;
    private final ArtifactService artifactService;

    private final Map<Transformation, Future<?>> tasks = new HashMap<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Autowired
    public TransformationServiceImpl(
        TransformationDao transformationDao,
        PluginService pluginService,
        @Lazy CsarDao csarDao,
        ArtifactService artifactService
    ) {
        this.transformationDao = transformationDao;
        this.pluginService = pluginService;
        this.csarDao = csarDao;
        this.artifactService = artifactService;
    }

    @Override
    public Transformation createTransformation(Csar csar, Platform targetPlatform) throws PlatformNotFoundException {
        return transformationDao.create(csar, targetPlatform);
    }

    @Override
    public boolean startTransformation(Transformation transformation) {
        //Only start the transformation if the input has been validated or the 
        //transformation does not need any additional properties
        if (transformation.getState() == TransformationState.READY) {
            Future<?> taskFuture = executor.submit(
                new ExecutionTask(
                    transformation,
                    artifactService,
                    pluginService,
                    csarDao.getContentDir(transformation.getCsar()),
                    transformationDao.getContentDir(transformation)
                )
            );
            tasks.put(transformation, taskFuture);
            return true;
        }
        return false;
    }

    @Override
    public boolean abortTransformation(Transformation transformation) {
        Future<?> task = tasks.get(transformation);
        //Return false 
        if (task == null) {
            return false;
        }
        //Return false because the transformation has already finished
        if (task.isDone()) {
            return false;
        }
        return task.cancel(true);
    }

    @Override
    public boolean deleteTransformation(Transformation transformation) {
        if (transformation.getState() == TransformationState.TRANSFORMING) {
            return false;
        }
        transformationDao.delete(transformation);
        tasks.remove(transformation);
        return true;
    }
}
