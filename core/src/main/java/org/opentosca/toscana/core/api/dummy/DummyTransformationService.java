package org.opentosca.toscana.core.api.dummy;

import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.transformation.Platform;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.TransformationService;

public class DummyTransformationService implements TransformationService {
	@Override
	public void createTransformation(Csar csar, Platform targetPlatform) {
		
	}

	@Override
	public boolean startTransformation(Transformation transformation) {
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
