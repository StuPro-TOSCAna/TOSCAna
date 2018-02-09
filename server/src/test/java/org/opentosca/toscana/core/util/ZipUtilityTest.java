package org.opentosca.toscana.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Random;
import java.util.zip.ZipInputStream;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.testdata.TestCsars;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ZipUtilityTest extends BaseUnitTest {

    private static final Logger log = LoggerFactory.getLogger(ZipUtilityTest.class);

    private static final int FILE_SIZE = 1024;

    private File original;
    private File unzipped;
    //Using seeded random to always get the same data
    private final Random rnd = new Random(123456);

    @Before
    public void setUp() throws Exception {
        original = new File(tmpdir, "original");
        unzipped = new File(tmpdir, "unzipped");
        unzipped.mkdirs();
        original.mkdirs();

        //generate folders Total 25 Directories with 10 files each (250 files)

        generateFolderStructure(original, 2, 10, 5, rnd);
    }

    @Test
    public void unzipFile() throws IOException {
        ZipInputStream is = new ZipInputStream(new FileInputStream(TestCsars.VALID_LAMP_INPUT));
        boolean result = ZipUtility.unzip(is, tmpdir.toString());
        assertTrue(result);
    }

    @Test
    public void unzipNotAFile() throws IOException {
        ZipInputStream is = new ZipInputStream(new FileInputStream(TestCsars.VALID_LAMP_INPUT_TEMPLATE));
        boolean result = ZipUtility.unzip(is, tmpdir.toString());
        assertFalse(result);
    }

    public void compressionTest() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ZipUtility.compressDirectory(original, out);

        //unzip the file
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        ZipUtility.unzip(new ZipInputStream(in), unzipped.getAbsolutePath());

        //Validating folder structure
        validateFolderStructure(unzipped, unzipped, original);
    }

    private void validateFolderStructure(File current, File currRoot, File compRoot) throws Exception {
        if (current.isFile()) {
            String relative = getRelativePath(currRoot, current);
            File comp = new File(compRoot, relative);
            log.trace("Comparing files {} and {}", current.getAbsolutePath(), comp.getAbsolutePath());
            assertTrue(comp.isFile() && comp.length() == current.length());
            byte[] currentData = Files.readAllBytes(current.toPath());
            byte[] compareData = Files.readAllBytes(comp.toPath());
            for (int i = 0; i < currentData.length; i++) {
                assertTrue("Mismatch at " + i, currentData[i] == compareData[i]);
            }
        } else {
            for (File file : current.listFiles()) {
                validateFolderStructure(file, currRoot, compRoot);
            }
        }
    }

    private String getRelativePath(File root, File subDir) {
        return root.toPath().relativize(subDir.toPath()).toString();
    }

    public static void generateFolderStructure(
        File current,
        int depth,
        int fileCount,
        int subdirs,
        Random rnd
    ) throws IOException {
        if (depth == 0) {
            return;
        }
        log.trace("Creating Directory {}", current.getAbsolutePath());
        current.mkdirs();

        //generate random files
        for (int i = 0; i < fileCount; i++) {
            byte[] data = new byte[FILE_SIZE];
            rnd.nextBytes(data);
            File file = new File(current, "rnd-" + i + ".bin");
            log.trace("Creating File {}", file.getAbsolutePath());
            Files.write(file.toPath(), data);
        }

        //generate directory structure
        for (int i = 0; i < subdirs; i++) {
            generateFolderStructure(
                new File(current, "folder-" + i),
                depth - 1,
                fileCount,
                subdirs,
                rnd
            );
        }
    }
}
