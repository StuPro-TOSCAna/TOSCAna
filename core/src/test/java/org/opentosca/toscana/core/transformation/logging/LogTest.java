package org.opentosca.toscana.core.transformation.logging;

import ch.qos.logback.classic.Level;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class LogTest {

    private Logger logger = LoggerFactory.getLogger(LogTest.class);

    private Log log;

    @Before
    public void setUp() throws Exception {
        log = new Log();
        logger.info("Creating dummy log entries");
        for (int i = 0; i < 100; i++) {
            log.addLogEntry(new LogEntry(String.format("Log-Message-%d", i), Level.DEBUG));
        }
    }

    @Test
    public void getAllLogEntries() throws Exception {
        logger.info("Trying to retrieve complete log");
        List<LogEntry> logs = log.getLogEntries(0);
        logger.info("Checking length");
        assertTrue(logs.size() == 100);
        logger.info("Checking data");
        for (int i = 0; i < logs.size(); i++) {
            LogEntry e = logs.get(i);
            assertTrue(e.getMessage().equals(String.format("Log-Message-%d", i)));
        }
        logger.info("Done");
    }

    @Test
    public void getPartialLogEntries() throws Exception {
        logger.info("Trying to log from index 50");
        List<LogEntry> logs = log.getLogEntries(50);
        logger.info("Checking length");
        assertTrue(logs.size() == 50);
        logger.info("Checking data");
        for (int i = 50; i < logs.size(); i++) {
            LogEntry e = logs.get(i);
            assertTrue(e.getMessage().equals(String.format("Log-Message-%d", i)));
        }
        logger.info("Done");
    }

    @Test
    public void getLogsFromOuterBound() throws Exception {
        logger.info("Trying to get logs from index 100");
        assertTrue(log.getLogEntries(101).size() == 0);
        logger.info("Done");
    }

    @Test
    public void getFirstTenLogEntries() throws Exception {
        logger.info("Trying to log from index 0 to 10");
        List<LogEntry> logs = log.getLogEntries(0, 9);
        logger.info("Checking length");
        assertTrue(logs.size() == 10);
        logger.info("Checking data");
        for (int i = 0; i < logs.size(); i++) {
            LogEntry e = logs.get(i);
            assertTrue(e.getMessage().equals(String.format("Log-Message-%d", i)));
        }
        logger.info("Done");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getLogEntriesWithInvalidBounds() throws Exception {
        logger.info("Trying to log from index 0 to 10");
        log.getLogEntries(100, 10);
    }
}
