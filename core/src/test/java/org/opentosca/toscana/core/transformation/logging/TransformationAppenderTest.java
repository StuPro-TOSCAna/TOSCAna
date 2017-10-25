package org.opentosca.toscana.core.transformation.logging;

import java.io.IOException;
import java.util.List;

import ch.qos.logback.classic.Logger;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertTrue;

public class TransformationAppenderTest {

    private Logger logger = (Logger) LoggerFactory.getLogger(getClass());

    private Logger testLogger;
    private Log log;

    @Before
    public void setUp() throws Exception {
        logger.info("Initializing test Logger");
        testLogger = (Logger) LoggerFactory.getLogger("TEST_LOGGER");
        log = new Log();
        //Initialize Logger
        logger.info("Adding transformation appender");
        TransformationAppender appender = new TransformationAppender(log);
        appender.start();
        appender.setContext(testLogger.getLoggerContext());
        testLogger.addAppender(appender);
        logger.info("Initialisation done");
    }

    @Test
    public void testAppender() throws Exception {
        int logCount = 15;
        logger.info("Logging some messages!");
        for (int i = 0; i < logCount; i++) {
            testLogger.info("Test Message {}", i);
            Thread.sleep(5);
        }
        logger.info("Checking list");
        List<LogEntry> logs = log.getLogEntries(0);
        logger.info("Logs length {}", logs.size());
        assertTrue(logs.size() == logCount);
        long timestamp = 0;
        for (int i = 0; i < logs.size(); i++) {
            assertTrue(timestamp < logs.get(i).getTimestamp());
            assertTrue(logs.get(i).getMessage().equals(String.format("Test Message %d", i)));
            logger.info("Logger Message: {}", logs.get(i).getMessage());
        }
    }

    @Test
    public void testAppenderStackTrace() throws Exception {
        Exception e = new RuntimeException("Test exception");
        logger.info("Test Exception", e);
        testLogger.info("Something went wrong", e);
        logger.info("Retrieving exception data");
        List<LogEntry> logs = log.getLogEntries(0);
        logger.info("Log Contents");
        for (int i = 0; i < logs.size(); i++) {
            LogEntry entry = logs.get(i);
            if (i == 0) {
                assertTrue(entry.getMessage().equals("Something went wrong"));
            } else if (i == 1) {
                assertTrue("java.lang.RuntimeException: Test exception".equals(entry.getMessage()));
            } else if (i == 2) {
                assertTrue(entry.getMessage().startsWith("at org.opentosca.toscana"));
            }
            logger.info("Line {}: {}", i + 1, entry.getMessage());
        }
    }

    @Test
    public void testAppenderStackTraceWithCause() throws Exception {
        Exception root = new IOException("Root Exception");
        Exception e = new RuntimeException("Test exception", root);
        logger.info("Test Exception", e);
        testLogger.info("Something went wrong", e);
        logger.info("Retrieving exception data");
        List<LogEntry> logs = log.getLogEntries(0);
        logger.info("Log Contents");
        int causeStart = -1;
        for (int i = 0; i < logs.size(); i++) {
            LogEntry entry = logs.get(i);
            if (i == 0) {
                assertTrue(entry.getMessage().equals("Something went wrong"));
            } else if (i == 1) {
                assertTrue("java.lang.RuntimeException: Test exception".equals(entry.getMessage()));
            } else if (i == 2) {
                assertTrue(entry.getMessage().startsWith("at org.opentosca.toscana"));
            } else if (entry.getMessage().equals("Cause:")) {
                causeStart = i;
            } else if ((causeStart + 1) == i) {
                assertTrue(entry.getMessage().equals("java.io.IOException: Root Exception"));
            } else if ((causeStart + 2) == i) {
                assertTrue(entry.getMessage().startsWith("at org.opentosca.toscana"));
            }
            logger.info("Line {}: {}", i + 1, entry.getMessage());
        }
        assertTrue(causeStart > 3);
    }
}
