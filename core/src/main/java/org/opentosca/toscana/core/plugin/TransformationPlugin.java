package org.opentosca.toscana.core.plugin;

import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.platform.Platform;

public interface TransformationPlugin {
	Platform getPlatformDetails();
	void transform(Transformation transformation) throws Exception;
	
}
