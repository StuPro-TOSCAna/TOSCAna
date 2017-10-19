package org.opentosca.toscana.core.plugin;

import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class SamplePlugin implements TransformationPlugin {
	
	@Override
	public Platform getPlatformDetails() {
		return new Platform("sample", "Sample Platform", new HashSet<>());
	}

	@Override
	public void transform(Transformation transformation) throws Exception {

	}
}
