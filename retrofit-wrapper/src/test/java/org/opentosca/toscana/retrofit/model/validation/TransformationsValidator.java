package org.opentosca.toscana.retrofit.model.validation;

import org.opentosca.toscana.retrofit.model.Transformation;
import org.opentosca.toscana.retrofit.model.embedded.TransformationResources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TransformationsValidator implements IModelValidator {
    @Override
    public void validate(Object object) {
        TransformationResources resources = (TransformationResources) object;
        assertEquals(1, resources.getContent().size());
        Transformation t = resources.getContent().get(0);
        assertNotNull(t.getPlatform());
        assertNotNull(t.getState());
        assertEquals(6, t.getLinks().size());
    }
}
