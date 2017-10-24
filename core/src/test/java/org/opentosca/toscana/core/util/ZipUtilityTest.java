package org.opentosca.toscana.core.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.util.Random;
import java.util.zip.ZipInputStream;

import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class ZipUtilityTest {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private File dir = new File("test-temp");

    private File original = new File(dir, "original");
    private File unzipped = new File(dir, "unzipped");

    private Random rnd = new Random(123456);

    private static final int FILE_SIZE = 256 * 1024;

    @Before
    public void setUp() throws Exception {
        //Cleanup
        FileUtils.delete(dir);
        dir.mkdirs();
        unzipped.mkdirs();
        original.mkdirs();

        //generate folders Total 125 Directories with 10 files each (1250 files)

        generateFolderStructure(original, 2, 10, 5, rnd);

    }

    @Test
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
            log.debug("Comparing files {} and {}", current.getAbsolutePath(), comp.getAbsolutePath());
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

    private String getRelativePath(File root, File subdir) {
        return root.toPath().relativize(subdir.toPath()).toString();
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.delete(dir);
    }

    private void generateFolderStructure(
        File current,
        int depth,
        int fileCount,
        int subdirs,
        Random rnd
    ) throws IOException {
        if (depth == 0) {
            return;
        }
        log.debug("Creating Directory {}", current.getAbsolutePath());
        current.mkdirs();

        //generate random files
        for (int i = 0; i < fileCount; i++) {
            byte[] data = new byte[FILE_SIZE];
            rnd.nextBytes(data);
            File file = new File(current, "rnd-" + i + ".bin");
            log.debug("Creating File {}", file.getAbsolutePath());
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
