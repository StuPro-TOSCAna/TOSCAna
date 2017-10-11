package org.opentosca.toscana.core.api.dummy;

import org.opentosca.toscana.core.logging.Log;
import org.opentosca.toscana.core.transformation.*;
import org.opentosca.toscana.core.transformation.artifacts.TargetArtifact;

public class DummyTransformation implements Transformation {
	
	private TransformationState state = TransformationState.QUEUED;
	private Platform platform;
	private Log log = new DummyLog();

	public DummyTransformation(Platform platform) {
		this.platform = platform;
	}

	@Override
	public TransformationState getState() {
		return state;
	}

	@Override
	public Platform getPlatform() {
		return platform;
	}

	@Override
	public Log getLog() {
		return log;
	}

	@Override
	public TargetArtifact getTargetArtifact() {
		return new TargetArtifact();
	}

	@Override
	public void setOnStateChange(TransformationListener listener) {

	}

	@Override
	public void removeOnStateChange(TransformationListener listener) {

	}
}
