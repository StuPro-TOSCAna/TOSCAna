package org.opentosca.toscana.core;

import java.io.File;

import org.opentosca.toscana.core.testutils.CategoryAwareJUnitRunner;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

/**
 * If files need to get written on disk during tests, use {@link #temporaryFolder}
 */
@RunWith(CategoryAwareJUnitRunner.class)
public abstract class BaseJUnitTest extends BaseTest {

    /**
     * Grants disk access. Is reset before every test method.
     */
    @ClassRule
    public static TemporaryFolder temporaryFolder = new TemporaryFolder(new File(System.getProperty("user.dir")));
    // "user.dir" is module root
    public static File tmpdir;
    
    @BeforeClass
    public static void initTmpdir(){
        tmpdir = temporaryFolder.getRoot();
    }
    
    @AfterClass
    public static void cleanUpDisk(){
        temporaryFolder.delete();
    }

}
