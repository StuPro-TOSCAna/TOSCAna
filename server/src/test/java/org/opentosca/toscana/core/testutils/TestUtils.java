package org.opentosca.toscana.core.testutils;

import org.opentosca.toscana.core.transformation.logging.Log;

import org.slf4j.LoggerFactory;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestUtils {
    /**
     This method creates a mocked log object that just creates regular loggers using LoggerFactory.getLogger()
     */
    public static Log getMockLog() {
        Log mockLog = mock(Log.class);
        when(mockLog.getLogger((Class<?>) any(Class.class)))
            .thenAnswer((iom) -> LoggerFactory.getLogger(((Class) iom.getArgument(0))));
        when(mockLog.getLogger(anyString()))
            .thenAnswer((iom) -> LoggerFactory.getLogger(iom.getArgument(0).toString()));
        return mockLog;
    }
}
