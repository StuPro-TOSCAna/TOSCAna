package org.opentosca.toscana.core.testutils;

import org.opentosca.toscana.core.transformation.logging.Log;

import org.slf4j.LoggerFactory;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestUtils {
    public static Log getMockLog() {
        Log mockLog = mock(Log.class);
        when(mockLog.getLogger((Class<?>) any(Class.class)))
            .thenReturn(LoggerFactory.getLogger("Log Mock Logger"));
        return mockLog;
    }
}
