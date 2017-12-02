package org.opentosca.toscana.core.transformation.logging;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.opentosca.toscana.core.BaseUnitTest;

import ch.qos.logback.classic.Logger;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class MemoryAppenderTest extends BaseUnitTest {

    private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());

    private Logger testLogger;
    private Log log;

    @Before
    public void setUp() throws Exception {
        logger.info("Initializing test Logger");
        testLogger = (Logger) LoggerFactory.getLogger("TEST_LOGGER");
        log = new LogImpl(new File(tmpdir, "log"));
        //Initialize Logger
        logger.info("Adding transformation appender");
        MemoryAppender appender = new MemoryAppender(log);
        appender.start();
        appender.setContext(testLogger.getLoggerContext());
        testLogger.addAppender(appender);
        logger.info("Initialization done");
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
        assertSame(logCount, logs.size());
        long timestamp = 0;
        for (int i = 0; i < logs.size(); i++) {
            assertTrue(timestamp < logs.get(i).getTimestamp());
            assertEquals(String.format("Test Message %d", i), logs.get(i).getMessage());
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
                assertEquals("Something went wrong", entry.getMessage());
            } else if (i == 1) {
                assertEquals("java.lang.RuntimeException: Test exception", entry.getMessage());
            } else if (i == 2) {
                assertTrue(entry.getMessage().startsWith("\tat org.opentosca.toscana"));
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
                assertEquals("Something went wrong", entry.getMessage());
            } else if (i == 1) {
                assertEquals("java.lang.RuntimeException: Test exception", entry.getMessage());
            } else if (i == 2) {
                assertTrue(entry.getMessage().startsWith("\tat org.opentosca.toscana"));
            } else if (entry.getMessage().equals("Cause:")) {
                causeStart = i;
            } else if ((causeStart + 1) == i) {
                assertEquals("java.io.IOException: Root Exception", entry.getMessage());
            } else if ((causeStart + 2) == i) {
                assertTrue(entry.getMessage().startsWith("\tat org.opentosca.toscana"));
            }
            logger.info("Line {}: {}", i + 1, entry.getMessage());
        }
        assertTrue(causeStart > 3);
    }
}
