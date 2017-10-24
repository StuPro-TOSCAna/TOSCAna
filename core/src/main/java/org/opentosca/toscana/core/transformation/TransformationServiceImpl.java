package org.opentosca.toscana.core.transformation;

import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarDao;
import org.opentosca.toscana.core.plugin.PluginService;
import org.opentosca.toscana.core.transformation.execution.ExecutionTask;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.properties.RequirementType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class TransformationServiceImpl implements TransformationService {

    public Logger log = LoggerFactory.getLogger(getClass());

    private final TransformationDao transformationDao;
    private CsarDao csarDao;
    private final PluginService pluginService;

    private Map<Transformation, Future<?>> tasks = new HashMap<>();
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Autowired
    public TransformationServiceImpl(TransformationDao transformationDao, PluginService pluginService) {
        this.transformationDao = transformationDao;
        this.pluginService = pluginService;
    }

    @Override
    public void createTransformation(Csar csar, Platform targetPlatform) {
        transformationDao.create(csar, targetPlatform);
    }

    @Override
    public boolean startTransformation(Transformation transformation) {
        //Only start the transformation if the input has been validated or the 
        //transformation does not need any addidtional properties
        if (transformation.getState() == TransformationState.CREATED
            || (transformation.getState() == TransformationState.INPUT_REQUIRED
            && transformation.isAllPropertiesSet(RequirementType.TRANSFORMATION))) {
            Future<?> taskFuture = executor.submit(
                new ExecutionTask(transformation, this, pluginService, csarDao.getContentDir(transformation.getCsar()),
                    transformationDao.getRootDir(transformation)));
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

    @Autowired
    public void setCsarDao(CsarDao csarDao){
        this.csarDao = csarDao;
    }
}
