package org.opentosca.toscana.core.transformation;

import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarDao;
import org.opentosca.toscana.core.plugin.PluginService;
import org.opentosca.toscana.core.transformation.execution.ExecutionTask;
import org.opentosca.toscana.core.transformation.platform.Platform;
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
public class TransformationServiceImpl
	implements TransformationService {

	public Logger log = LoggerFactory.getLogger(getClass());

	private final CsarDao csarDao;
	private final PluginService pluginService;
	
	private Map<Transformation, Future<?>> tasks = new HashMap<>();
	private ExecutorService executor = Executors.newSingleThreadExecutor();

	@Autowired
	public TransformationServiceImpl(CsarDao csarDao, PluginService pluginService) {
		this.csarDao = csarDao;
		this.pluginService = pluginService;
	}

	@Override
	public void createTransformation(Csar csar, Platform targetPlatform) {
		Transformation transformation = new TransformationImpl(csar, targetPlatform);

		//TODO Implement check to find out if inputs are needed

		csar.getTransformations().put(targetPlatform.id, transformation);
	}

	@Override
	public boolean startTransformation(Transformation transformation) {
		if (transformation.getState() == TransformationState.CREATED
			&& transformation.getState() == TransformationState.INPUT_REQUIRED) {
			Future<?> taskFuture =executor.submit(
				new ExecutionTask(transformation, this, pluginService));
			tasks.put(transformation, taskFuture);
			return true;
		}
		return false;
	}

	@Override
	public boolean abortTransformation(Transformation transformation) {
		return false;
	}

	@Override
	public boolean deleteTransformation(Transformation transformation) {
		return false;
	}

}
