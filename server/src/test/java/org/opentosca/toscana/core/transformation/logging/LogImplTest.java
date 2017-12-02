package org.opentosca.toscana.core.transformation.logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.opentosca.toscana.core.BaseUnitTest;

import ch.qos.logback.classic.Level;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class LogImplTest extends BaseUnitTest {

    private final Logger logger = LoggerFactory.getLogger(LogImplTest.class);
    private Log log;
    private File logfile;

    @Before
    public void setUp() throws Exception {
        log = new LogImpl(new File(tmpdir, "log"));
        logger.info("Creating dummy log entries");
        for (int index = 0; index < 100; index++) {
            LogEntry logEntry = new LogEntry(index, String.format("Log-Message-%d", index), Level.DEBUG);
            log.addLogEntry(logEntry);
        }
        logfile = new File(tmpdir, "log");
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
            assertEquals((String.format("Log-Message-%d", i)), e.getMessage());
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
            assertEquals(String.format("Log-Message-%d", i), e.getMessage());
        }
        logger.info("Done");
    }

    @Test
    public void getLogsFromOuterBound() throws Exception {
        logger.info("Trying to get logs from index 100");
        assertSame(0, log.getLogEntries(101).size());
        logger.info("Done");
    }

    @Test
    public void getFirstTenLogEntries() throws Exception {
        logger.info("Trying to log from index 0 to 10");
        List<LogEntry> logs = log.getLogEntries(0, 9);
        logger.info("Checking length");
        assertSame(10, logs.size());
        logger.info("Checking data");
        for (int i = 0; i < logs.size(); i++) {
            LogEntry e = logs.get(i);
            assertEquals(String.format("Log-Message-%d", i), e.getMessage());
        }
        logger.info("Done");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getLogEntriesWithInvalidBounds() throws Exception {
        logger.info("Trying to log from index 0 to 10");
        log.getLogEntries(100, 10);
    }

    @Test
    public void readLogEntriesFromDisk() throws IOException {
        log = new LogImpl(logfile);
        Logger testLogger = log.getLogger("my-test-context");
        testLogger.info("produce a first valid log line");
        Log testLog = new LogImpl(logfile);
        List<LogEntry> entries = testLog.getLogEntries(0);
        assertEquals(1, entries.size());
        testLogger.info("produce a second valid log line");
        testLog = new LogImpl(logfile);
        entries = testLog.getLogEntries(0);
        assertEquals(2, entries.size());
    }

    @Test
    public void readLogEntriesFromDiskSetLevelCorrectly() {
        log = new LogImpl(logfile);
        Logger testLogger = log.getLogger("my-test-context");
        String[] messages = {"info message", "warn message", "error message"};
        testLogger.info(messages[0]);
        testLogger.warn(messages[1]);
        testLogger.error(messages[2]);
        Log readFromDiskLog = new LogImpl(logfile);
        List<LogEntry> logEntries = readFromDiskLog.getLogEntries(0);
        assertEquals(3, logEntries.size());
        assertTrue(logEntries.get(0).getMessage().contains(messages[0]));
        assertEquals(Level.INFO.toString(), logEntries.get(0).getLevel());
        assertTrue(logEntries.get(1).getMessage().contains(messages[1]));
        assertEquals(Level.WARN.toString(), logEntries.get(1).getLevel());
        assertTrue(logEntries.get(2).getMessage().contains(messages[2]));
        assertEquals(Level.ERROR.toString(), logEntries.get(2).getLevel());
    }

    @Test
    public void readLogEntriesFromDiskSetTimestampCorrectly() throws InterruptedException {
        log = new LogImpl(logfile);
        Logger testLogger = log.getLogger(getClass());
        testLogger.info("testing timestamps now");
        long expected = log.getLogEntries(0).get(0).getTimestamp();
        Log testLog = new LogImpl(logfile);
        long result = testLog.getLogEntries(0).get(0).getTimestamp();
        // the delta is necessary because in rare cases there was an offset of 1ms
        // this offset is acceptable and not worth fixing
        assertEquals(expected, result, 1);
    }

    @Test
    public void logReadsLogfileWithIllegalLogsAndIgnoresThem() throws IOException {
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(logfile)));
        pw.write("log level which does not adhere to logging format");
        log = new LogImpl(logfile);
        List<LogEntry> entries = log.getLogEntries(0);
        assertEquals(0, entries.size());
    }

    @Test
    public void logReadsLogfileWithStacktraces() {
        log = new LogImpl(logfile);
        Logger testLogger = log.getLogger(getClass());
        try {
            throw new ArithmeticException();
        } catch (ArithmeticException e) {
            testLogger.error("printing stacktrace to log", e);
        }
        Log readFromDiskLog = new LogImpl(logfile);
        List<LogEntry> logEntries = readFromDiskLog.getLogEntries(0);
        for (int index = 0; index < logEntries.size(); index++) {
            LogEntry entry = logEntries.get(index);
            assertNotEquals(0, entry.getTimestamp());
            assertEquals(index, entry.getIndex());
            assertEquals(Level.ERROR.toString(), entry.getLevel());
        }
    }
}
