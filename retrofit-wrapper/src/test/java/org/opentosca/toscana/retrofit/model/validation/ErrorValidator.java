package org.opentosca.toscana.retrofit.model.validation;

import org.opentosca.toscana.retrofit.model.ServerError;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ErrorValidator implements IModelValidator {
    private boolean withLogs;

    public ErrorValidator(boolean withLogs) {
        this.withLogs = withLogs;
    }

    @Override
    public void validate(Object object) {
        ServerError error = (ServerError) object;
        assertNotNull(error.getStatus());
        assertNotNull(error.getTimestamp());
        assertNotNull(error.getPath());
        if (withLogs) {
            assertNotNull(error.getException());
            assertNotNull(error.getLogs());
            assertEquals(10, error.getLogs().size());
        }
    }
}
