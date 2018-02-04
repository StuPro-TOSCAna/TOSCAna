package org.opentosca.toscana.retrofit.model.validation;

import org.opentosca.toscana.retrofit.model.TransformationInputs;
import org.opentosca.toscana.retrofit.model.TransformationProperty;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class GetInputsValidator
    implements IModelValidator {

    @Override
    public void validate(Object obj) {
        TransformationInputs inputs = (TransformationInputs) obj;
        assertEquals(6, inputs.getInputs().size());
        inputs.getInputs().forEach(e -> assertNull(e.getValue()));
        assertEquals(6,
            inputs.getInputs().stream().filter(TransformationProperty::isRequired).count());
        assertEquals(6, inputs.getInputs().stream().filter(e -> e.getDescription() != null).count());
        assertEquals(6, inputs.getInputs().stream().filter(e -> e.getKey() != null).count());
        assertEquals(6, inputs.getInputs().stream().filter(e -> e.getType() != null).count());
    }
}
