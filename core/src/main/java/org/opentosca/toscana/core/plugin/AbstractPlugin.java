package org.opentosca.toscana.core.plugin;

import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.platform.Platform;


public abstract class AbstractPlugin implements TransformationPlugin {
	@Override
	public Platform getPlatformDetails() {
		return null;
	}

	@Override
	public void transform(Transformation transformation) throws Exception {

	}
}
