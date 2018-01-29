package org.opentosca.toscana.core;

import java.io.File;

import org.opentosca.toscana.UnitTest;
import org.opentosca.toscana.core.transformation.logging.Log;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ActiveProfiles({Profiles.EXCLUDE_BASE_IMAGE_MAPPER})
@Category(UnitTest.class)
public abstract class BaseTest {

    // "user.dir" is module root
    protected static final File PROJECT_ROOT = new File(System.getProperty("user.dir"));
    protected static Log log;
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @BeforeClass
    public static void setupLog() {
        log = mock(Log.class);
        when(log.getLogger(anyString())).thenReturn(LoggerFactory.getLogger("test logger"));
        when(log.getLogger(any(Class.class))).thenReturn(LoggerFactory.getLogger("test logger"));
    }
}
