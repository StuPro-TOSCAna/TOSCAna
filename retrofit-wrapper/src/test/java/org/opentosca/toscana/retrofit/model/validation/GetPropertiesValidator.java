package org.opentosca.toscana.retrofit.model.validation;

import org.opentosca.toscana.retrofit.model.TransformationProperties;
import org.opentosca.toscana.retrofit.model.TransformationProperty;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class GetPropertiesValidator
    implements IModelValidator {
    
    @Override
    public void validate(Object obj) {
        TransformationProperties object = (TransformationProperties) obj;
        assertEquals(6, object.getProperties().size());
        object.getProperties().forEach(e -> assertNull(e.getValue()));
        assertEquals(6,
            object.getProperties().stream().filter(TransformationProperty::isRequired).count());
        assertEquals(6, object.getProperties().stream().filter(e -> e.getDescription() != null).count());
        assertEquals(6, object.getProperties().stream().filter(e -> e.getKey() != null).count());
        assertEquals(6, object.getProperties().stream().filter(e -> e.getType() != null).count());
    }
}
