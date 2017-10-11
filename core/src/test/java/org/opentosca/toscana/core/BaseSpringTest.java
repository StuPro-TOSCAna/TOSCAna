package org.opentosca.toscana.core;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.runner.RunWith;
import org.opentosca.toscana.core.util.Preferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Extend from this class in order to inherit important configurations
 * Sets up Spring Test Context regarding to the Test Configuration
 * After every test method, refreshes the context.
 * After every test method, deletes written files from disk
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Component
public abstract class BaseSpringTest {

	private final static Logger logger = LoggerFactory.getLogger(BaseSpringTest.class);

	@Autowired
	protected Preferences preferences;

	@After
	public void tearDown() throws Exception {
		FileUtils.deleteDirectory(preferences.getDataDir());
		logger.info("cleaned up disk");
	}
}
