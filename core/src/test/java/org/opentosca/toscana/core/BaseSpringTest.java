package org.opentosca.toscana.core;

import java.io.IOException;

import org.opentosca.toscana.core.testutils.CategoryAwareSpringRunner;
import org.opentosca.toscana.core.util.Preferences;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/**
 * Extend from this class in order to inherit important configurations Sets up Spring Test Context regarding to the Test
 * Configuration After every test method, refreshes the context. After every test method, deletes written files from
 * disk
 */
@RunWith(CategoryAwareSpringRunner.class)
@ContextConfiguration(classes = {TestCoreConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("dummy_plugins")
@Component
@TestPropertySource("classpath:test-properties.yml")
public abstract class BaseSpringTest extends BaseTest {

    private final static Logger logger = LoggerFactory.getLogger(BaseSpringTest.class);

    @Autowired
    protected Preferences preferences;

    @Before
    @After
    public void cleanUpDisk() throws IOException {
        FileUtils.deleteDirectory(preferences.getDataDir());
        preferences.getDataDir().mkdirs();
        logger.info("cleaned up disk");
    }
}
