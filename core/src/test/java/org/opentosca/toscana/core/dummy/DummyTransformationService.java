    package org.opentosca.toscana.core.dummy;

import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.TransformationService;
import org.opentosca.toscana.core.transformation.TransformationState;
import org.opentosca.toscana.core.transformation.platform.Platform;

public class DummyTransformationService implements TransformationService {
    private boolean returnValue = true;
    
    private boolean startReturnValue = false;

    @Override
    public Transformation createTransformation(Csar csar, Platform targetPlatform) {
        System.out.println("Creating Transformation for " + csar.getIdentifier() + " on " + targetPlatform.id);
        Transformation t = new DummyTransformation((targetPlatform));
        csar.getTransformations().put(targetPlatform.id, t);
        return t;
    }

    public void setDeleteReturnValue(boolean returnValue) {
        this.returnValue = returnValue;
    }

    public void setStartReturnValue(boolean startReturnValue) {
        this.startReturnValue = startReturnValue;
    }

    @Override
    public boolean startTransformation(Transformation transformation) {
        if(startReturnValue) {
            transformation.setState(TransformationState.TRANSFORMING);
        }
        return startReturnValue;
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
