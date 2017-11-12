package org.opentosca.toscana.retrofit.model.validation;

import org.opentosca.toscana.retrofit.model.TransformationLogs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LogValidator implements IModelValidator {
    @Override
    public void validate(Object object) {
        TransformationLogs logs = (TransformationLogs) object;
        assertEquals(10, logs.getLogEntries().size());
        assertEquals(new Long(0), logs.getStart());
        assertEquals(new Long(9), logs.getEnd());
        assertEquals(10,
            logs.getLogEntries().stream()
                .filter(e -> e.getMessage() != null && e.getLevel() != null && e.getTimestamp() > 0).count()
        );
        assertNotNull(logs.getLinks().get("self"));
        assertEquals(2, logs.getLinks().size());
    }
}
