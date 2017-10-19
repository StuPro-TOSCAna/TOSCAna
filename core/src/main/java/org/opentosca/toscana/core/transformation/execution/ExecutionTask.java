package org.opentosca.toscana.core.transformation.execution;

import org.opentosca.toscana.core.plugin.PluginService;
import org.opentosca.toscana.core.plugin.TransformationPlugin;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.TransformationService;
import org.opentosca.toscana.core.transformation.TransformationState;
import org.opentosca.toscana.core.util.SystemStatus;
import org.slf4j.Logger;

public class ExecutionTask implements Runnable {

	private Transformation transformation;
	private TransformationService transformationService;
	private TransformationPlugin plugin;

	private Logger log;

	public ExecutionTask(
		Transformation transformation,
		TransformationService transformationService,
		PluginService pluginService
	) {
		this.transformation = transformation;
		this.transformationService = transformationService;
		this.log = transformation.getTransformationLogger(getClass());
		this.plugin = pluginService.findPluginByPlatform(transformation.getPlatform());
	}

	@Override
	public void run() {
		log.info("Starting Transformation executor for {}/{}",
			transformation.getCsar().getIdentifier(),
			transformation.getPlatform().id);
		transformationService.setSystemStatus(SystemStatus.TRANSFORMING);
		try {
			plugin.transform(transformation);
		} catch (Exception e) {
			log.error("Transforming of {}/{} has errored!",
				transformation.getCsar().getIdentifier(),
				transformation.getPlatform().id);
			log.error("Error message: ", e);
			transformation.setState(TransformationState.ERROR);
		}
		transformationService.setSystemStatus(SystemStatus.IDLE);
	}
}
