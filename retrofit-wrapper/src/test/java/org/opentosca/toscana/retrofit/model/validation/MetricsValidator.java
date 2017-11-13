package org.opentosca.toscana.retrofit.model.validation;

import java.util.Map;

import static junit.framework.TestCase.assertTrue;

public class MetricsValidator implements IModelValidator {
    @Override
    public void validate(Object object) {
        Map<String, Object> values = (Map<String, Object>) object;
        assertTrue(values.size() > 10);
    }
}
