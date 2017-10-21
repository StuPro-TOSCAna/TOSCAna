package org.opentosca.toscana.core.dummy;

import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.TransformationService;

//@Service //TODO If Transformation Service has been implemented
public class DummyTransformationService implements TransformationService {
	private boolean returnValue = true;
	@Override
	public void createTransformation(Csar csar, Platform targetPlatform) {
		System.out.println("Creating Transformation for " + csar.getIdentifier() + " on " + targetPlatform.id);
		csar.getTransformations().put(targetPlatform.id, new DummyTransformation(targetPlatform));
	}

	public void setReturnValue(boolean returnValue) {
		this.returnValue = returnValue;
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
		
		return returnValue;
	}
	
}
