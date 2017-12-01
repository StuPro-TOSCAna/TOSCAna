package org.opentosca.toscana.core;

import java.io.File;

import org.opentosca.toscana.core.testutils.TestCategories;
import org.opentosca.toscana.core.testutils.TestCategory;

import org.junit.Rule;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles( {Profiles.EXCLUDE_BASE_IMAGE_MAPPER})
@TestCategory(value = TestCategories.FAST)
public abstract class BaseTest {

    // "user.dir" is module root
    protected static final File PROJECT_ROOT = new File(System.getProperty("user.dir"));

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
}
