package org.opentosca.toscana.core;

import java.io.File;

import org.opentosca.toscana.UnitTest;

import org.junit.Rule;
import org.junit.experimental.categories.Category;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({Profiles.EXCLUDE_BASE_IMAGE_MAPPER})
@Category(UnitTest.class)
public abstract class BaseTest {

    // "user.dir" is module root
    protected static final File PROJECT_ROOT = new File(System.getProperty("user.dir"));

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
}
