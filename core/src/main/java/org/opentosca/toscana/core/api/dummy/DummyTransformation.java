package org.opentosca.toscana.core.api.dummy;

import org.opentosca.toscana.core.logging.Log;
import org.opentosca.toscana.core.transformation.*;

public class DummyTransformation implements Transformation {
	
	private TransformationState state = TransformationState.QUEUED;
	private Platform platform;

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
		return null;
	}

	@Override
	public void setOnStateChange(TransformationListener listener) {

	}

	@Override
	public void removeOnStateChange(TransformationListener listener) {

	}
}
