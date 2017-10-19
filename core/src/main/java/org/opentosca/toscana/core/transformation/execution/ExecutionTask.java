package org.opentosca.toscana.core.transformation.execution;

import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.TransformationService;

public class ExecutionTask implements Runnable {

	private Transformation transformation;
	private TransformationService transformationService;

	public ExecutionTask(
		Transformation transformation,
		TransformationService transformationService
	) {
		this.transformation = transformation;
		this.transformationService = transformationService;
	}

	@Override
	public void run() {

	}
}
