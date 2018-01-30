package org.opentosca.toscana.core.transformation.logging;

import java.io.File;
import java.io.IOException;

import org.opentosca.toscana.core.BaseUnitTest;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import static org.junit.Assert.assertTrue;

public class PersistentAppenderTest extends BaseUnitTest {

    private File logfile;
    private Log log;
    private Logger logger;

    @Before
    public void setUp() {
        logfile = new File(tmpdir, "log");
    }

    @Test
    public void appenderWritesToFile() throws IOException, InterruptedException {
        log = new LogImpl(logfile);
        logger = log.getLogger(getClass());
        String message1 = "this message should get written to logfile";
        logger.info(message1);
        String result = FileUtils.readFileToString(logfile);
        assertTrue(result.contains(message1));
    }

    @Test
    public void appenderWritesStackTracesToFile() throws IOException {
        log = new LogImpl(logfile);
        logger = log.getLogger(getClass());
        try {
            throw new ArithmeticException();
        } catch (ArithmeticException e) {
            logger.error("testing stacktraces", e);
        }
        String result = FileUtils.readFileToString(logfile);
        String[] lines = result.split("\n");
        assertTrue(lines.length > 15);
    }
}
