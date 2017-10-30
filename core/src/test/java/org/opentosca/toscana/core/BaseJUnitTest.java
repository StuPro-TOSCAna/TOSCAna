package org.opentosca.toscana.core;

import java.io.File;
import java.io.IOException;

import org.opentosca.toscana.core.testutils.CategoryAwareJUnitRunner;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

/**
 If files need to get written on disk during tests, use {@link #temporaryFolder}
 */
@RunWith(CategoryAwareJUnitRunner.class)
public abstract class BaseJUnitTest extends BaseTest {

    public static final File PROJECT_ROOT = new File(System.getProperty("user.dir"));

    /**
     Grants disk access. Is reset before every test method.
     */
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder(PROJECT_ROOT);
    // "user.dir" is module root
    public File tmpdir;

    @Before
    public final void initTmpdir() {
        tmpdir = temporaryFolder.getRoot();
        tmpdir.deleteOnExit();
    }

    @AfterClass
    public static void cleanUpDisk() throws IOException, InterruptedException {
        File[] files = PROJECT_ROOT.listFiles((file1, s) -> s.matches("junit[0-9]+"));
        for (File file : files) {
            FileUtils.deleteDirectory(file);
        }
    }
}
