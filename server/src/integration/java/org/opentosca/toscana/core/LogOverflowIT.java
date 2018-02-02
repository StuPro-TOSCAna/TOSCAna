package org.opentosca.toscana.core;

import java.io.File;

import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.logging.LogImpl;

import org.junit.Test;
import org.slf4j.Logger;

@SuppressWarnings("Duplicates")
public class LogOverflowIT extends BaseIntegrationTest {

    @Test
    public void testLogOverflow() {
        Log logFactory = new LogImpl(new File(tmpdir, "testlog.log"));
        int i = 1;
        while (i < 5000) {
            i++;
            Logger l = logFactory.getLogger("Logger-" + i);
            if (i % 500 == 0) {
                l.info("Log Message {}", i);
            }
        }
        logFactory.close();
    }
}
