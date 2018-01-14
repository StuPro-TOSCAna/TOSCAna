package org.opentosca.toscana.core;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.opentosca.toscana.core.util.Preferences;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 Extend from this class in order to inherit important configurations Sets up Spring Test Context regarding to the Test
 Configuration After every test method, refreshes the context.
 If disk access is needed, use {@link #tmpdir}
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestCoreConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource("classpath:test-properties.yml")
public abstract class BaseSpringTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(BaseSpringTest.class);

    protected File tmpdir;
    @Autowired
    private Preferences preferences;

    /**
     temp data dir. if files need to get written to disk, use this as root directory. This folder is reset after every
     test method. Appropriate disk cleanup is performed automatically.
     */

    @PostConstruct
    private void initTmpdir() {
        tmpdir = preferences.getDataDir();
    }

    @Before
    public final void startupPrepareDisk() throws IOException {
        FileUtils.deleteDirectory(tmpdir);
        tmpdir.mkdirs();
        logger.info("Cleaned up disk");
    }

    @After
    public final void shutdownCleanUpDisk() throws IOException {
        FileUtils.deleteDirectory(tmpdir);
        logger.info("Clean up disk");
    }
}
