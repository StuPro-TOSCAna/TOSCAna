package org.opentosca.toscana.core.dummy;

import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.TransformationService;
import org.opentosca.toscana.core.transformation.platform.Platform;

//@Service //TODO If Transformation Service has been implemented
public class DummyTransformationService implements TransformationService {
    private boolean returnValue = true;

    @Override
    public Transformation createTransformation(Csar csar, Platform targetPlatform) {
        System.out.println("Creating Transformation for " + csar.getIdentifier() + " on " + targetPlatform.id);
        Transformation t = new DummyTransformation((targetPlatform));
        csar.getTransformations().put(targetPlatform.id, t);
        return t;
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
